package com.gykj.visitor.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.arcsoft.liveness.LivenessEngine;
import com.gykj.acface.common.Constants;
import com.gykj.acface.customview.FaceRectView;
import com.gykj.acface.model.DrawInfo;
import com.gykj.acface.util.CameraHelper;
import com.gykj.acface.util.CameraListener;
import com.gykj.acface.util.DrawHelper;
import com.gykj.acface.util.ImageUtil;
import com.gykj.visitor.R;
import com.gykj.visitor.entity.IDCardEntity;
import com.gykj.visitor.entity.ParentEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.utils.SharedPreferencedUtils;
import com.gykj.visitor.views.IFaceAuthView;
import com.lanzhu.baidutts.manager.BaiduTtsManager;
import com.orhanobut.logger.Logger;
import com.wrs.gykjewm.baselibrary.base.BaseActivity;
import com.wrs.gykjewm.baselibrary.common.Constant;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.utils.ImageUtils;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * desc   : 人脸认证界面
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/2312:14
 * version: 1.0
 */
public class FaceAuthActivity extends BaseActivity implements IFaceAuthView,CameraListener {

    private static final String TAG = FaceAuthActivity.class.getSimpleName();
    @BindView(R.id.textureview_preview)
    TextureView mFacePreView;

    @BindView(R.id.facerect_view)
    FaceRectView mFaceRectView;

    @BindView(R.id.face_circle_iv)
    ImageView mFaceCircleIv;

    /**
     * 认证方式
     */
    private int AUTH_TYPE;
    private ParentEntity parentEntity;

    /**
     * 访客人员id
     */
    private long visitor_id = -1;
    /**
     * 访客状态
     */
    private int visitor_type = 0;
    private IDCardEntity mIDCardInfo;

    private VisitorEntity mEntity;


    CameraHelper cameraHelper;
    DrawHelper drawHelper;
    Camera.Size previewSize;
    Integer cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    FaceEngine faceEngine;
    int afCode = -1;
    int processMask =  FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER |FaceEngine.ASF_LIVENESS ;


    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    private boolean isRecognize = false;

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


    private FaceFeature[] faceFeatures;
    private byte bgr24[];
    private FaceFeature mIdCardFaceFeature;
    private boolean mIdCardRecognize = false;

    private final static int HAS_MORE_PEOPLE = 101;

    private int people = 0;

    private boolean singlePeople;


    private FaceTask mFaceTask;


    /**火体检测**/
    private LivenessEngine livenessEngine;
    private boolean isfaceTaskbool = true;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HAS_MORE_PEOPLE:
                    people ++;
                    if(people >=20){
                        if(!singlePeople){
                            //此时有两人站在中心点以上，不能进行人脸支付，确保只有一人站在识别框内
                            BaiduTtsManager.getManager().speak("请不要多人在识别框内");
                        }
                        people = 0;
                    }
                    break;
            }
        }
    };


    //用固定线程试试  效果
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    @Override
    public int initContentView() {
        return R.layout.activity_face_auth;
    }

    @Override
    public void initData() {
        AUTH_TYPE = getIntent().getExtras().getInt(Constant.AUTH_TYPE);
        parentEntity = (ParentEntity) getIntent().getExtras().getSerializable(Constant.PARENT_INFO);

     //   bgr24 = getIntent().getExtras().getByteArray(Constant.FACE_BITMAP);
        faceFeatures = Constant.FACEFEATURES;

        mIDCardInfo = (IDCardEntity) getIntent().getExtras().getSerializable(Constant.ID_CARD_INFO);
        mEntity = (VisitorEntity) getIntent().getExtras().getSerializable(Constant.VISITOR);
        visitor_type = getIntent().getExtras().getInt(Constant.VISITOR_TYPE);
        livenessEngine = new LivenessEngine();
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }
    }

    @Override
    public void initUi() {
        switch (AUTH_TYPE){
            case Constant.IDCARD:
                BaiduTtsManager.getManager().speak("请进行人证对比");
                break;
            case Constant.QRCODE:
                BaiduTtsManager.getManager().speak("请进行人脸采集");
                break;
        }
        mFaceRectView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mfaceRectViewWidth = mFaceRectView.getWidth();
                mfaceRectViewHeight = mFaceRectView.getHeight();
            }
        });

         startRotation();

    }

    @OnClick({R.id.face_close_iv})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.face_close_iv:
                finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    public void startRotation() {
        //设置旋转的样式
        ObjectAnimator animtorAlpha = ObjectAnimator.ofFloat(mFaceCircleIv, "rotation", 0f, 360f);
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
    public void intoActivity() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                BaiduTtsManager.getManager().speak("人脸信息认证成功");
            }
        });
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.ID_CARD_INFO,mIDCardInfo);
        bundle.putSerializable(Constant.VISITOR,mEntity);
        switch (visitor_type){
            case 1:
                skipActivity(FaceAuthActivity.this,NoAppointActivity.class,bundle);
                break;
            case 2:
                skipActivity(FaceAuthActivity.this,AppointActivity.class,bundle);
                break;
        }
    }

    @Override
    public void intoScanActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.PARENT_INFO,parentEntity);
        skipActivity(FaceAuthActivity.this,ScanResultActivity.class,bundle);
    }

    @Override
    public void initEngine() {
        if(null == faceEngine){
            faceEngine = new FaceEngine();
            afCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_VIDEO, FaceEngine.ASF_OP_0_HIGHER_EXT,
                    16, 20, FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS | FaceEngine.ASF_FACE_RECOGNITION);
            VersionInfo versionInfo = new VersionInfo();
            faceEngine.getVersion(versionInfo);
            Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);


            //激活活体引擎   是否是第一次激活   只需要激活一次

           int liveness =   SharedPreferencedUtils.getInt(Constants.LIVENESS_INIT,0);
           if(liveness == 0)
           {
               Executors.newSingleThreadExecutor().execute(new Runnable() {
                   @Override
                   public void run() {
                       final long activeCode = livenessEngine.activeEngine(FaceAuthActivity.this, Constants.LIVENESSAPPID,
                               Constants.LIVENESSSDKKEY).getCode();
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               if(activeCode == com.arcsoft.liveness.ErrorInfo.MOK) {
                                   System.out.println("活体引擎激活成功");
                                   SharedPreferencedUtils.setInt(Constants.LIVENESS_INIT,1);
                                   initlivebess();
                               } else if(activeCode == com.arcsoft.liveness.ErrorInfo.MERR_AL_BASE_ALREADY_ACTIVATED){
                                   System.out.println("活体引擎已激活");
                               } else {
                                   System.out.println("活体引擎激活失败，errorcode：" + activeCode);
                               }
                           }
                       });
                   }
               });
           }else {
               initlivebess();
           }


        }
    }

    private void initlivebess() {
        //活体引擎初始化（图片）
        com.arcsoft.liveness.ErrorInfo error = livenessEngine.initEngine(FaceAuthActivity.this, LivenessEngine.AL_DETECT_MODE_IMAGE);
        if(error.getCode() != com.arcsoft.liveness.ErrorInfo.MOK) {
            System.out.println("活体初始化失败，errorcode：" + error.getCode());
            return;
        }
    }

    @Override
    public void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        cameraHelper = new CameraHelper.Builder()
                .metrics(metrics)
                .rotation(2)
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(mFacePreView)
                .cameraListener(this)
                .build();
        cameraHelper.init();
    }

    @Override
    public boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
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

    @Override
    public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
        Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
        previewSize = camera.getParameters().getPreviewSize();
        drawHelper = new DrawHelper(previewSize.width, previewSize.height, mFacePreView.getWidth(), mFacePreView.getHeight(), displayOrientation
                , cameraId, isMirror);
        }

    @Override
    public void onPreview(byte[] data, Camera camera) {
        if(!isResume && !mIdCardRecognize){
            return;
        }
       if (mFaceRectView != null) {
            mFaceRectView.clearFaceInfo();
        }
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = faceEngine.detectFaces(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);

        if (code == ErrorInfo.MOK) {
            code = faceEngine.process(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
            if (code != ErrorInfo.MOK) {
                return;
            }
        }
        Log.i("HXS","开始对比图片");

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

        if (drawHelper != null) {
            List<DrawInfo> drawInfoList = new ArrayList<>();
            for (int i = 0; i < faceInfoList.size(); i++) {
                drawInfoList.add(new DrawInfo(faceInfoList.get(i).getRect(), genderInfoList.get(i).getGender(), ageInfoList.get(i).getAge(), faceLivenessInfoList.get(i).getLiveness()));
            }
            drawHelper.draw(mFaceRectView, drawInfoList);
        }

        //借助AsyncTask开启一个线程在后台处理数据


        if(isfaceTaskbool)
        {
            if(null != mFaceTask){
                switch(mFaceTask.getStatus()){
                    case RUNNING:
                        return;
                    case PENDING:
                        mFaceTask.cancel(false);
                        break;
                }
            }
            mFaceTask = new FaceTask(data,faceInfoList);
            mFaceTask.execute((Void)null);
        }
/*
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = faceEngine.detectFaces(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);

        if (code == ErrorInfo.MOK) {
            code = faceEngine.process(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
            if (code != ErrorInfo.MOK) {
                return;
            }
        }
       *//* for (int i = 0; i < faceInfoList.size(); i++) {
            Log.i(TAG, "onPreview:  face[" + i + "] = " + faceInfoList.get(i).toString());
        }*//*
        List<AgeInfo> ageInfoList = new ArrayList<>();
        List<GenderInfo> genderInfoList = new ArrayList<>();
        List<Face3DAngle> face3DAngleList = new ArrayList<>();
        List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();
        int ageCode = faceEngine.getAge(ageInfoList);
        int genderCode = faceEngine.getGender(genderInfoList);
        int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
        int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);

        *//**
         * 有其中一个的错误码不为0，return
         *//*
        if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
            return;
        }
        if (mFaceRectView != null && drawHelper != null) {
            List<DrawInfo> drawInfoList = new ArrayList<>();
            for (int i = 0; i < faceInfoList.size(); i++) {
                drawInfoList.add(new DrawInfo(faceInfoList.get(i).getRect(), genderInfoList.get(i).getGender(), ageInfoList.get(i).getAge(), faceLivenessInfoList.get(i).getLiveness()));
            }
            drawHelper.draw(mFaceRectView, drawInfoList);
            if(isRecognize){
                return;
            }
            center_people = 0;
            for(int i = 0;i<faceInfoList.size();i++){
                if(checkIsCenter(faceInfoList.get(i).getRect())){
                    center_people++;
                }
            }

            if(center_people > 1){
                singlePeople = false;
                mHandler.sendEmptyMessage(HAS_MORE_PEOPLE);
                return;
            }
            people = 0;
            singlePeople = true;
            for(int i = 0;i<faceInfoList.size();i++){
                if(checkIsCenter(faceInfoList.get(i).getRect())){
                    isRecognize = true;
                    switch (AUTH_TYPE){
                        case Constant.IDCARD:
                            FaceFeature faceFeature = new FaceFeature();
                            int res = faceEngine.extractFaceFeature(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList.get(i), faceFeature);
                            if (res == 0) {
                                FaceSimilar faceSimilar = new FaceSimilar();
                           *//*     mIdCardFaceFeature = new FaceFeature();
                                //bgr24  身份证识别  人物头像的bity特征数组
                                mIdCardFaceFeature.setFeatureData(bgr24);*//*
                                int compareResult = faceEngine.compareFaceFeature(faceFeatures[0], faceFeature, faceSimilar);
                                Log.i("HXS","人脸相似度:"+faceSimilar.getScore());
                                if (faceSimilar.getScore() >= 0.56f) {  //
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            intoActivity();
                                        }
                                    });
                                    break;
                                }
                            }
                            isRecognize = false;
                            break;
                        case Constant.QRCODE:
                            Bitmap user_bitmap = ImageUtils.getBitmapFromNv21(data, previewSize.width, previewSize.height, faceInfoList.get(i).getRect());
                            ImageUtils.saveBitmap(user_bitmap,"user.png");
                            intoScanActivity();
                            break;

                    }
                }
            }

        }*/

       // initbitmap(data);
    }

    private void toast(String data) {
        Log.e("HXS",data);
    }

    private  class FaceTask extends AsyncTask<Void ,Void,Void>{
        private byte[] mData;
        private List<FaceInfo> mFaceInfoLists;
        //构造函数
        FaceTask(byte[] data, List<FaceInfo> faceInfoList){
            this.mData = data;
            this.mFaceInfoLists = faceInfoList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
             processImage(mData,mFaceInfoLists);
            return null;
        }
    }

    private void initbitmap(final byte[] data) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                        //processImage(data);
                     }catch (Exception e)
                    {

                    }

                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void processImage(byte[] data ,List<FaceInfo> faceInfoList)
    {
        if(isRecognize){
            return;
        }
        final List<com.arcsoft.liveness.FaceInfo> faceInfos = new ArrayList<>();

        for (int i = 0; i < faceInfoList.size(); i++) {
            faceInfos.add(new com.arcsoft.liveness.FaceInfo(faceInfoList.get(i).getRect(), faceInfoList.get(i).getOrient()));
        }

        //活体检测(目前只支持单人脸)
        List<com.arcsoft.liveness.LivenessInfo> livenessInfos = new ArrayList<>();
        //先活体检测
        com.arcsoft.liveness.ErrorInfo livenessError = livenessEngine.startLivenessDetect(data, previewSize.width, previewSize.height,
                LivenessEngine.CP_PAF_NV21, faceInfos, livenessInfos);
        if (livenessError.getCode() == com.arcsoft.liveness.ErrorInfo.MOK) {
            if(livenessInfos.size() == 0) {
                toast("无人脸");
                return;
            }
            final int liveness = livenessInfos.get(0).getLiveness();
            Log.d(TAG, "getLivenessScore: liveness " + liveness);
            if(liveness == com.arcsoft.liveness.LivenessInfo.NOT_LIVE) {
                toast("非活体");
                return;
            } else if(liveness == com.arcsoft.liveness.LivenessInfo.LIVE) {
                toast("活体");
                center_people = 0;
                for(int i = 0;i<faceInfoList.size();i++){
                    if(checkIsCenter(faceInfoList.get(i).getRect())){
                        center_people++;
                    }
                }
                if(center_people > 1){
                    singlePeople = false;
                    mHandler.sendEmptyMessage(HAS_MORE_PEOPLE);
                    return;
                }

                people = 0;
                singlePeople = true;
                for(int i = 0;i<faceInfoList.size();i++){
                    //   if(checkIsCenter(faceInfoList.get(i).getRect())){
                    isRecognize = true;
                    switch (AUTH_TYPE){
                        case Constant.IDCARD:
                            FaceFeature faceFeature = new FaceFeature();
                            int res = faceEngine.extractFaceFeature(data, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList.get(i), faceFeature);
                            if (res == 0) {
                                FaceSimilar faceSimilar = new FaceSimilar();
                           /*     mIdCardFaceFeature = new FaceFeature();
                                //bgr24  身份证识别  人物头像的bity特征数组
                                mIdCardFaceFeature.setFeatureData(bgr24);*/
                                int compareResult = faceEngine.compareFaceFeature(faceFeatures[0], faceFeature, faceSimilar);
                                Log.i("HXS","人脸相似度:"+faceSimilar.getScore());
                                if (faceSimilar.getScore() >= 0.46f) {  //
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            isfaceTaskbool = false;
                                            intoActivity();
                                        }
                                    });
                                    break;
                                }
                            }else {
                                return;
                            }

                            break;
                        case Constant.QRCODE:
                            Bitmap user_bitmap = ImageUtils.getBitmapFromNv21(data, previewSize.width, previewSize.height, faceInfoList.get(i).getRect());
                            ImageUtils.saveBitmap(user_bitmap,"user.png");
                            intoScanActivity();
                            break;

                    }
                    // }
                }
                isRecognize = false;

            } else if(liveness == com.arcsoft.liveness.LivenessInfo.MORE_THAN_ONE_FACE) {
                toast("非单人脸信息");
            } else {
                toast("未知");
            }
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

        //活体引擎销毁
        if(livenessEngine!=null)
        {
            livenessEngine.unInitEngine();
        }
        if (afCode == 0) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }
    @Override
    protected void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
         if(null != mFaceTask){
            mFaceTask.cancel(false);
        }
        unInitEngine();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();

    }
}
