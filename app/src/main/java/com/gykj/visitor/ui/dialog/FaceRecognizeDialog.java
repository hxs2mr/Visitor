package com.gykj.visitor.ui.dialog;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.gykj.acface.customview.FaceRectView;
import com.gykj.acface.model.DrawInfo;
import com.gykj.acface.util.CameraHelper;
import com.gykj.acface.util.CameraListener;
import com.gykj.acface.util.DrawHelper;

import com.gykj.acface.util.ImageUtil;
import com.gykj.visitor.R;

import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.utils.ScreenUtils;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;



/**
 * description:
 * <p>
 * author: josh.lu
 * created: 16/8/18 下午12:59
 * email:  1113799552@qq.com
 * version: v1.0
 */
@SuppressLint("ValidFragment")
public class FaceRecognizeDialog extends DialogFragment implements CameraListener {

    private static final String TAG = FaceRecognizeDialog.class.getSimpleName();

    private TextureView textureViewPreview;
    private FaceRectView faceRectView;
    private ImageView mDialogCloseIv;
    private ImageView mDialogCircleIv;

    CameraHelper cameraHelper;
    DrawHelper drawHelper;
    Camera.Size previewSize;
    Integer cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    FaceEngine faceEngine;
    int afCode = -1;
    int processMask =  FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;


    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    private boolean isRecognize = false;

    private final static int RECOGNIZED = 100;

    /**
     * 未找到人脸计数器
     */
    private int mCount = 0;
    /**
     * 关闭框计数器
     */
    private int close_count = 0;

    /**
     * 活体检测计数器
     */
    private int alive_count = 0;

    private int close_live_count = 0;


    private boolean isResume = false;

    /**
     * 判断中心点人数
     */
    private int center_people = 0;


    private int mfaceRectViewWidth;
    private int mfaceRectViewHeight;


    private byte bgr24[];
    private FaceFeature mIdCardFaceFeature;
    private boolean mIdCardRecognize = false;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 设置背景透明
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // 去掉标题 死恶心死恶心的
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set cancel on touch outside
        getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.dialog_face_login_layout, container,false);
        initView(rootView);
        initData();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        //params.width = ScreenUtils.dip2px(getActivity(),854);
        params.width = ScreenUtils.dip2px(getActivity(),854);
        params.height = ScreenUtils.dip2px(getActivity(),480);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }

    private void initData() {
        isRecognize = false;
        isResume = false;
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
    }

    private void initView(View view) {
        textureViewPreview = view.findViewById(R.id.textureview_preview);
        faceRectView = view.findViewById(R.id.facerect_view);
        mDialogCloseIv = view.findViewById(R.id.dialog_close_iv);
        mDialogCircleIv = view.findViewById(R.id.dialog_circle_iv);
        startRotation();
        mDialogCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        faceRectView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mfaceRectViewWidth = faceRectView.getWidth();
                mfaceRectViewHeight = faceRectView.getHeight();
            }
        });
    }

    private void initEngine() {
        if(null == faceEngine){
            faceEngine = new FaceEngine();
            afCode = faceEngine.init(getActivity(), FaceEngine.ASF_DETECT_MODE_VIDEO, FaceEngine.ASF_OP_0_HIGHER_EXT,
                    16, 20, FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS | FaceEngine.ASF_FACE_RECOGNITION);
            VersionInfo versionInfo = new VersionInfo();
            faceEngine.getVersion(versionInfo);
            Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);
        }
    }

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        cameraHelper = new CameraHelper.Builder()
                .metrics(metrics)
                .rotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(textureViewPreview)
                .cameraListener(this)
                .build();
        cameraHelper.init();
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(getActivity(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                initEngine();
                initCamera();
                if (cameraHelper != null) {
                    cameraHelper.start();
                }
            } else {
                ToastUtils.showToast(ToastType.WARNING, "请打开相机权限");
            }
        }
    }





    public void startRotation() {
        //设置旋转的样式
        ObjectAnimator animtorAlpha = ObjectAnimator.ofFloat(mDialogCircleIv, "rotation", 0f, 360f);
        //旋转不停顿
        animtorAlpha.setInterpolator(new AccelerateDecelerateInterpolator());
        //设置动画重复次数
        animtorAlpha.setRepeatCount(-1);
        //旋转时长
        animtorAlpha.setDuration(2000);
        //开始旋转
        animtorAlpha.start();
    }




    @Override
    public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
        Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
        previewSize = camera.getParameters().getPreviewSize();
        drawHelper = new DrawHelper(previewSize.width, previewSize.height, textureViewPreview.getWidth(), textureViewPreview.getHeight(), displayOrientation
                , cameraId, true);
    }

    @Override
    public void onPreview(byte[] data, Camera camera) {
        if(!isResume && !mIdCardRecognize){
            return;
        }
        if (faceRectView != null) {
            faceRectView.clearFaceInfo();
        }
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = faceEngine.detectFaces(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);

        if (code == ErrorInfo.MOK) {
            code = faceEngine.process(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
            if (code != ErrorInfo.MOK) {
                return;
            }
        }
        for (int i = 0; i < faceInfoList.size(); i++) {
            Log.i(TAG, "onPreview:  face[" + i + "] = " + faceInfoList.get(i).toString());
        }
        List<AgeInfo> ageInfoList = new ArrayList<>();
        List<GenderInfo> genderInfoList = new ArrayList<>();
        List<Face3DAngle> face3DAngleList = new ArrayList<>();
        List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();
        int ageCode = faceEngine.getAge(ageInfoList);
        int genderCode = faceEngine.getGender(genderInfoList);
        int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
        int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);

        /**
         * 有其中一个的错误码不为0，return
         */
        if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
            return;
        }
        if (faceRectView != null && drawHelper != null) {
            List<DrawInfo> drawInfoList = new ArrayList<>();
            for (int i = 0; i < faceInfoList.size(); i++) {
                drawInfoList.add(new DrawInfo(faceInfoList.get(i).getRect(), genderInfoList.get(i).getGender(), ageInfoList.get(i).getAge(), faceLivenessInfoList.get(i).getLiveness()));
            }
            drawHelper.draw(faceRectView, drawInfoList);
            if(isRecognize){
                return;
            }
            center_people = 0;
            for(int i = 0;i<faceInfoList.size();i++){
                if(checkIsCenter(faceInfoList.get(i).getRect())){
                    center_people++;
                    if(center_people > 1){
                        center_people = 0;
                        //此时有两人站在中心点以上，不能进行人脸支付，确保只有一人站在识别框内
                        ToastUtils.showToast(ToastType.WARNING,"识别框内不能有多人识别");
                        break;
                    }
                }
            }
            for(int i = 0;i<faceInfoList.size();i++){
                if(checkIsCenter(faceInfoList.get(i).getRect())){
                    //检查是否是真人还是图片
                    if(faceLivenessInfoList.get(i).getLiveness() != LivenessInfo.ALIVE){
                        alive_count ++;
                        if(alive_count == 30){
                            ToastUtils.showToast(ToastType.WARNING,"请用真实人脸进行支付");
                            alive_count = 0;
                            close_live_count ++;
                            if(close_live_count == 3){
                                dismiss();
                                close_live_count = 0;
                            }
                        }
                        break;
                    }
                    isRecognize = true;
                    FaceFeature faceFeature = new FaceFeature();

                    int res = faceEngine.extractFaceFeature(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList.get(i), faceFeature);
                    if (res == 0) {
                        FaceSimilar faceSimilar = new FaceSimilar();
                        long start = System.currentTimeMillis();
                        int compareResult = faceEngine.compareFaceFeature(mIdCardFaceFeature, faceFeature, faceSimilar);
                        if (faceSimilar.getScore() >= 0.82f) {
                            System.out.println("=======相似度====" + faceSimilar.getScore());
                            dismiss();
                            break;
                            }
                        long end = System.currentTimeMillis();
                        System.out.println("=======识别时间====" + (end - start));
                    }
                    isRecognize = false;
                }
            }

        }
    }

    public void setFaceBitmap(Bitmap bitmap){
        //bitmap转bgr
        bgr24 = ImageUtil.bitmapToBgr(bitmap);
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = faceEngine.detectFaces(bgr24, bitmap.getWidth(), bitmap.getHeight(), FaceEngine.CP_PAF_NV21, faceInfoList);

        if (code == ErrorInfo.MOK) {
            code = faceEngine.process(bgr24, bitmap.getWidth(), bitmap.getHeight(), FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
            if (code != ErrorInfo.MOK) {
                return;
            }
        }
        for (int i = 0; i < faceInfoList.size(); i++) {
            Log.i(TAG, "onPreview:  face[" + i + "] = " + faceInfoList.get(i).toString());
        }
        List<AgeInfo> ageInfoList = new ArrayList<>();
        List<GenderInfo> genderInfoList = new ArrayList<>();
        List<Face3DAngle> face3DAngleList = new ArrayList<>();
        List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();
        int ageCode = faceEngine.getAge(ageInfoList);
        int genderCode = faceEngine.getGender(genderInfoList);
        int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
        int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);

        /**
         * 有其中一个的错误码不为0，return
         */
        if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
            mIdCardRecognize = false;
            return;
        }
        mIdCardFaceFeature = new FaceFeature();
        if(faceInfoList.size()>= 0){
            int res = faceEngine.extractFaceFeature(bgr24, bitmap.getWidth(), bitmap.getWidth(), FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), mIdCardFaceFeature);
            if(res == 0){
                mIdCardRecognize = true;
            }else {
                mIdCardRecognize = false;
            }
        }else {
            mIdCardRecognize = false;
        }
    }

    /**
     * 判断是否中心点
     */
    public boolean checkIsCenter(Rect rect){
        float l = (mfaceRectViewWidth/2-100)*((float)previewSize.width/mfaceRectViewWidth);
        float t = (mfaceRectViewHeight/2-100)*((float)previewSize.height/mfaceRectViewHeight);
        float r = (mfaceRectViewWidth/2+100)*((float)previewSize.width/mfaceRectViewWidth);
        float b = (mfaceRectViewHeight/2+100)*((float)previewSize.height/mfaceRectViewHeight);
        if(rect.left >= l
                && rect.top >= t
                && rect.right <= r
                && rect.bottom <= b){
            return true;
        }
        return false;
    }

    @Override
    public void onCameraClosed() {
        Log.i(TAG, "onCameraClosed: ");
    }

    @Override
    public void onCameraError(Exception e) {
        Log.i(TAG, "onCameraError: " + e.getMessage());
    }

    @Override
    public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
        if (drawHelper != null) {
            drawHelper.setCameraDisplayOrientation(displayOrientation);
        }
        Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
    }


    private void unInitEngine() {
        if (afCode == 0) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }

    @Override
    public void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
        super.onDestroy();
    }
}
