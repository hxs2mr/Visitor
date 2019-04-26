package com.gykj.visitor.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.VersionInfo;
import com.guoguang.jni.idcard.IDCardMsg;
import com.guoguang.jni.idcard.IDCardRecognition;
import com.guoguang.jni.utils.FileUtils;
import com.gykj.acface.util.ImageUtil;
import com.gykj.visitor.R;
import com.gykj.visitor.entity.IDCardEntity;
import com.gykj.visitor.entity.ParentEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.presenter.MainPresenter;
import com.gykj.visitor.service.VisitorListService;
import com.gykj.visitor.views.IMainView;
import com.gykj.visitor.widget.MainMessageDialog;
import com.orhanobut.logger.Logger;
import com.wrs.gykjewm.baselibrary.base.BaseActivity;
import com.wrs.gykjewm.baselibrary.common.Constant;
import com.wrs.gykjewm.baselibrary.domain.ToastType;
import com.wrs.gykjewm.baselibrary.manager.VisitorManager;
import com.wrs.gykjewm.baselibrary.utils.ThreadManager;
import com.wrs.gykjewm.baselibrary.utils.ThreadSignManager;
import com.wrs.gykjewm.baselibrary.utils.ToastUtils;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import com.zkteco.android.biometric.module.idcard.meta.IDPRPCardInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wrs.gykjewm.baselibrary.common.Constant.PID;
import static com.wrs.gykjewm.baselibrary.common.Constant.VID;

public class MainActivity extends BaseActivity implements IMainView,TextWatcher{


    @BindView(R.id.main_qrcord_et)
    EditText mMainQrCordEt;

    @BindView(R.id.main_visitor_tv)
    TextView mMainVisitorNumberTv;

    @BindView(R.id.main_school_name_tv)
    TextView mMainSchoolNameTv;

    @BindView(R.id.test_img)
    AppCompatImageView test_img;


    private IDCardReader idCardReader = null;
    private UsbManager musbManager = null;
    private final String ACTION_USB_PERMISSION = "com.example.scarx.idcardreader.USB_PERMISSION";

    private boolean is_Recognize = false;

    MainPresenter presenter;

    VisitorBrocastReceiver mReceiver;

    private Intent mVisitorService;

    private IDCardInfo mIDCardInfo;  //身份证中你的包含的信息
    private FaceEngine faceEngine;
    private int faceEngineCode = -1;
    /**
     * 请求权限的请求码
     */
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;

    /**
     * 所需的所有权限信息
     */
    private static String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };
    private String TAG = MainActivity.class.getSimpleName();

   private MainMessageDialog  mainMessageDialog;

    /**
     * 新的USB身份证识别设备模块  设备二
     */

    private IDCardRecognition mIDCardRecognition;
    private IDCardMsg cardMsg;

    @Override
    public int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        mainMessageDialog = new MainMessageDialog(getContext(),this);
        presenter = new MainPresenter();
        presenter.attachView(this);

         startIDCardReader();          //初始化刷身份证
         //RequestDevicePermission();   //设备一  初始化USB的权限

        //  startRecognizeIDCard();   // 设备一  外接：进行身份证识别的设备
        initVisitorService();     //

        initFaceEngine();     //初始化人脸的权限

        presenter.noLeavingCount(); //获取未离校的人数

        Log.i("HXS","开始");
    }
    @Override
    public void initUi() {
        mMainQrCordEt.setInputType(InputType.TYPE_NULL);
        mMainQrCordEt.addTextChangedListener(this);
        if(null != VisitorManager.getManager().getSchoolName() && !TextUtils.isEmpty(VisitorManager.getManager().getSchoolName())){
            mMainSchoolNameTv.setText(VisitorManager.getManager().getSchoolName());
            mMainSchoolNameTv.setText(VisitorManager.getManager().getSchoolName());
        }
    }

    @OnClick({R.id.main_visitor_layout,R.id.main_message_fra})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.main_visitor_layout:
                showActivity(this,VisitorListActivity.class);
                break;
            case R.id.main_message_fra://点击了// 消息通知的提示  弹框
                mainMessageDialog
                        //.showAt
                        .show();
                break;
        }
    }

    /**
     * 以前的USB设备授权
     */
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    }
                    else {
                        ToastUtils.showToast(ToastType.WARNING,"USB未授权");
                    }
                }
            }
        }
    };


    @Override
    public void startIDCardReader() {
        // Define output log level
        /**设备一 外接身份证设备**/
      /*  LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map idrparams = new HashMap();
        idrparams.put(ParameterHelper.PARAM_KEY_VID, VID);
        idrparams.put(ParameterHelper.PARAM_KEY_PID, PID);
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.USB, idrparams);*/
        /**设备二  内嵌身份证设备**/
        mIDCardRecognition = new IDCardRecognition(MainActivity.this, mIDCardRecListener);
        mIDCardRecognition.start();  //设备二 内嵌
    }

    @Override
    public void RequestDevicePermission() {
        musbManager = (UsbManager)this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);


        registerReceiver(mUsbReceiver, filter);


        for (UsbDevice device : musbManager.getDeviceList().values()) {
            if (device.getVendorId() == VID && device.getProductId() == PID) {
                Intent intent = new Intent(ACTION_USB_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                musbManager.requestPermission(device, pendingIntent);
            }
        }
    }

    @Override
    public void startRecognizeIDCard() {
        try {
            idCardReader.open(0);  //打开设备
            ThreadSignManager.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    while (!is_Recognize){
                        try {
                            if(!idCardReader.getStatus(0)){  // 判断设备状态  false:异常

                                idCardReader.reset(0);  //重置设备
                            }
                            final IDCardInfo idCardInfo = new IDCardInfo();
                            boolean ret = false;

                            idCardReader.findCard(0);//寻卡
                            idCardReader.selectCard(0);//选卡

                            Thread.sleep(50);
                            ret = idCardReader.readCard(0, 0, idCardInfo);  //进行身份证读卡  取出信息放入 idcardinfo中
                            if (ret) {
                           /*     Message msg = new Message();
                                msg.obj =idCardInfo;
                                msg.what =1;
                                handler.sendMessage(msg);*/
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        is_Recognize = true;
                                        mIDCardInfo = idCardInfo;

                                        checkIsAppoint(mIDCardInfo);//身份证数据的读取和上传进行进校离校的判断
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("startRecognizeIDCard",e.getMessage());
                        }
                    }
                }
            });
        } catch (IDCardReaderException e) {
            e.printStackTrace();
            ToastUtils.showToast(ToastType.EEEOR,"打开设备失败");
            Log.d("startRecognizeIDCard",e.getMessage());
        }
    }

    //设备一 是否预约
    @Override
    public void checkIsAppoint(IDCardInfo info) {
        int gender = 0;
        if("男".equals(info.getSex())){
            gender = 1;
        }else {
            gender = 2;
        }
        //进行离校或进校 是否预约
    //    presenter.checkIsAppoint(info.getAddress(),info.getBirth(),info.getValidityTime(),info.getId(),info.getNation(),info.getDepart(),info.getName(),gender,info.getPhoto());
    }


    //设备二  是否预约
    @Override
    public void checkIsAppoint(IDCardMsg info) {
        int gender = 0;
        if("男".equals(info.getSex())){
            gender = 1;
        }else {
            gender = 2;
        }
        //进行离校或进校 是否预约
        presenter.checkIsAppoint(info.getAddress(),info.getBirthDate()+"",info.getUsefulStartDate().toString(),info.getIdCardNum(),info.getNationStr(),info.getSignOffice(),info.getName(),gender,info.getPortrait(),info.getRet());
    }

    @Override
    public void stopRecognize(boolean recognize) {
        this.is_Recognize = recognize;
    }

    @Override
    public void intoScanResultActivity(ParentEntity parentEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.PARENT_INFO,parentEntity);
        bundle.putInt(Constant.AUTH_TYPE,Constant.QRCODE);
        showActivity(this,FaceAuthActivity.class,bundle);
    }

    @Override
    public void initVisitorService() {
        mVisitorService = new Intent(   this, VisitorListService.class);
        startService(mVisitorService);
        mReceiver = new VisitorBrocastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_GET_VISITOR_LIST);
        registerReceiver(mReceiver, filter);
    }

    /**
     * 获取未离校的人数
     * @param count
     */
    @Override
    public void showVisitorCount(int count) {
        mMainVisitorNumberTv.setText(String.valueOf(count));
    }

    @Override
    public void initFaceEngine() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
        }
    }

    @Override
    public void initEngine() {

        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(this, FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_HIGHER_EXT,
                16, 10, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE| FaceEngine.ASF_LIVENESS);

        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        Log.i(TAG, "initEngine: init: " + faceEngineCode + "  version:" + versionInfo);

        if (faceEngineCode != ErrorInfo.MOK) {
            Toast.makeText(this, "初始化引擎失败", Toast.LENGTH_SHORT).show();
        }
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

    /**
     * @param bitmap
     * @param entity
     * @param code
     */
    @Override
    public void extractFeature( byte[] bitmap, VisitorEntity entity, int code) {
        if(bitmap == null)
        {
            return ;
        }
        initbitmap(bitmap , entity,  code);

     /*   //NV21宽度必须为4的倍数,高度为2的倍数
        bitmap = ImageUtil.alignBitmapForNv21(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //bitmap转NV21
        final byte[] nv21 = ImageUtil.bitmapToNv21(bitmap, width, height);

        if(null != nv21){
            List<FaceInfo> faceInfoList = new ArrayList<>();
            //人脸检测
            int detectCode = faceEngine.detectFaces(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList);

            if (detectCode != 0 || faceInfoList.size() == 0) {
                ToastUtils.showToast(ToastType.WARNING,"face detection finished, code is " + detectCode + ", face num is " + faceInfoList.size());
                return;
            }

            //人脸信息检测
            int faceProcessCode = faceEngine.process(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
            Log.i(TAG, "processImage: " + faceProcessCode);
            if (faceProcessCode != ErrorInfo.MOK) {
                ToastUtils.showToast(ToastType.WARNING,"face process finished, code is " + faceProcessCode);
                return;
            }

            FaceFeature mIdCardFaceFeature = new FaceFeature();
            if(faceInfoList.size()>= 0){
                int res = faceEngine.extractFaceFeature(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList.get(0), mIdCardFaceFeature);
                if (res != ErrorInfo.MOK) {
                    ToastUtils.showToast(ToastType.WARNING,"提取人脸特征失败");
                }else {
                    Bundle bundle = new Bundle();
                    switch (code){
                        case 1://未预约
                        case 2://已经预约
                            IDCardEntity mEntity = new IDCardEntity();
                            mEntity.setAddress(mIDCardInfo.getAddress());
                            mEntity.setId(mIDCardInfo.getId());
                            mEntity.setName(mIDCardInfo.getName());
                            mEntity.setNation(mIDCardInfo.getNation());
                            mEntity.setSex(mIDCardInfo.getSex());
                            mEntity.setBirth(mIDCardInfo.getBirth());
                            mEntity.setPhoto(mIDCardInfo.getPhoto());
                            bundle.putByteArray(Constant.FACE_BITMAP,mIdCardFaceFeature.getFeatureData());
                            bundle.putSerializable(Constant.ID_CARD_INFO,mEntity);
                            bundle.putSerializable(Constant.VISITOR,entity);
                            bundle.putInt(Constant.VISITOR_TYPE,code);
                            bundle.putInt(Constant.AUTH_TYPE,Constant.IDCARD);
                            showActivity(MainActivity.this,FaceAuthActivity.class,bundle);
                            break;
                        case 3://离开校门后向服务器确认
                            break;

                    }
                }
            }else {
                ToastUtils.showToast(ToastType.WARNING,"没有找到人脸信息1");
            }

        } else {
            ToastUtils.showToast(ToastType.WARNING,"没有找到人脸信息0");
        }*/
    }

    /**
     * 图形转换操作  比较耗时放入子线程中操作
     */
    private  void  initbitmap(final byte[] data , final VisitorEntity entity, final int code)
    {
        Log.i("HXS","准备执行");
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                processImage(data,code,entity);
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

    private void processImage(byte[] data,int code,VisitorEntity entity) {


        //获取图片的特征值
       // Bitmap mBitmap = IDPhotoHelper.Bgr2Bitmap(data);


        Bitmap mBitmap =BitmapFactory.decodeByteArray(data, 0, data.length);

        Bitmap bitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Log.i("HXS","开始执行");
        if (bitmap == null)
        {
            ToastUtils.showToast(ToastType.WARNING,"获取身份失败!  ");
            return;
        }

        bitmap = ImageUtil.alignBitmapForBgr24(bitmap);
        if(bitmap == null)
        {
            Log.i("HXS","图片转换失败");
            return;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //bitmap转bgr
        byte[]  bgr24 = ImageUtil.bitmapToBgr(bitmap);

        if(bgr24 ==null)
        {
            Log.i("HXS","转化bgr失败");
            return;
        }

        List<FaceInfo> faceInfoList =new ArrayList<>();

        /**
         * 成功获取bgr24的数据 开始人脸检测
         */
        long fdStartTime = System.currentTimeMillis();
        int detectCode = faceEngine.detectFaces(bgr24,width,height, FaceEngine.CP_PAF_BGR24, faceInfoList);


        if(detectCode == ErrorInfo.MOK)
        {
            Log.i("HXS","人脸信息检测所花时间:"+(System.currentTimeMillis() - fdStartTime));
        }
        if(faceInfoList.size()<=0)
        {
            Log.i("HXS","未获取到人脸信息");
            return;
        }

        /**
         * 根据获取到人脸位置何角度信息  传入process函数  进行年龄 性别三维角度检测
         */
            long processStartTime = System.currentTimeMillis();
            int faceprocessCode = faceEngine.process(bgr24,width,height, FaceEngine.CP_PAF_BGR24, faceInfoList, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE );

            if(faceprocessCode!=ErrorInfo.MOK)
            {
                Log.i("HXS","人脸检测 失败   code is " + faceprocessCode);
                return;
            }else {
                Log.i("HXS","process检测:"+(System.currentTimeMillis() - processStartTime));
            }
    //FaceFeature
        /**
         * 最后将图片内的所有人脸进行一一比对
         */
        if(faceInfoList.size() > 0 )
        {
            FaceFeature[] faceFeatures = new FaceFeature[faceInfoList.size()];
            int[] extractFaceFeatureCodes = new int[faceInfoList.size()];

            for (int  i= 0 ; i< faceInfoList.size() ; i++)
            {
                faceFeatures[i] = new FaceFeature();
                //从图片解析出人脸特征数据
                long frStartTimme= System.currentTimeMillis();
                extractFaceFeatureCodes[i] = faceEngine.extractFaceFeature(bgr24,width,height,FaceEngine.CP_PAF_BGR24, faceInfoList.get(i), faceFeatures[i]);
                if (extractFaceFeatureCodes[i]  != ErrorInfo.MOK) {
                    ToastUtils.showToast(ToastType.WARNING,"提取人脸特征失败");
                }
            }
            Log.i("HXS","提起特征值成功!");
            if(extractFaceFeatureCodes[0]!=ErrorInfo.MOK)
            {
                ToastUtils.showToast(ToastType.WARNING,"提取人脸特征失败!");
                return;
            }
            Log.i("HXS","准备跳转!");
            final Bundle bundle = new Bundle();
            switch (code){
                case 1://未预约
                case 2://已经预约
                    IDCardEntity mEntity = new IDCardEntity();
                    mEntity.setAddress(cardMsg.getAddress());
                    mEntity.setId(cardMsg.getIdCardNum());
                    mEntity.setName(cardMsg.getName());
                    mEntity.setNation(cardMsg.getNationStr());
                    mEntity.setSex(cardMsg.getSexStr());
                    mEntity.setBirth(cardMsg.getBirthDate().toString());
                    mEntity.setPhoto(cardMsg.getPortrait());
                    Constant.FACEFEATURES = faceFeatures;
                    bundle.putSerializable(Constant.ID_CARD_INFO,mEntity);
                    bundle.putSerializable(Constant.VISITOR,entity);
                    bundle.putInt(Constant.VISITOR_TYPE,code);
                    bundle.putInt(Constant.AUTH_TYPE,Constant.IDCARD);
                    Log.i("HXS","跳转");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          //  mIDCardRecognition.close();
                            showActivity(MainActivity.this,FaceAuthActivity.class,bundle);
                        }
                    });
                    break;
                case 3://离开校门后向服务器确认
                    Log.i("HXS","准备跳转3!");
                    break;
             }

        }else {
            Log.i("HXS","特征值获取未0");
        }


    }

    @Override
    public void setIDCardReaderRecognize(boolean recognize) {
        is_Recognize = false;
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
            } else {
                ToastUtils.showToast(ToastType.WARNING, "请打开相应权限");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("HXS","MainActivity开始活动!");

      //   startRecognizeIDCard();   //设备一 外接
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        is_Recognize = false;

        presenter.noLeavingCount();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onStop() {
        super.onStop();
        is_Recognize = true;
    }
    @Override
    protected void onDestroy() {

        //现在内嵌的身份证识别设备
        if(mIDCardRecognition != null)
        {
            mIDCardRecognition.close();
        }

        super.onDestroy();

        /**
         * 以前的外接设备（身份证识别）
         *//*
        if(null != idCardReader){
            IDCardReaderFactory.destroy(idCardReader);
        }*/

        if(presenter.isAttached()){
            presenter.cancel();
            presenter.detachView();
        }

       // ThreadManager.getInstance().shutdown();

        //设备授权
       //  unregisterReceiver(mUsbReceiver);

        unregisterReceiver(mReceiver);
        stopService(mVisitorService);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().contains("\\n")){
            String[] gzgykjs = s.toString().split("gzgykj_");
            if(gzgykjs.length > 0){
                String userId = gzgykjs[1].substring(0,gzgykjs[1].length()-2);
                presenter.getParentInfo(Long.valueOf(userId));
            }
            mMainQrCordEt.setText("");
        }
    }



    private class VisitorBrocastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constant.ACTION_GET_VISITOR_LIST)){
                String visitor_number = intent.getStringExtra(Constant.VISITOR_NUMBER);
                mMainVisitorNumberTv.setText(visitor_number);
            }
        }
    }

    /**
     * 设备二 内嵌身份证识别的回调
     */

    private IDCardRecognition.IDCardRecListener mIDCardRecListener = new IDCardRecognition.IDCardRecListener() {
        @Override
        public void onResp(IDCardMsg info) {
            if(info == null) {
                ToastUtils.showToast(ToastType.WARNING, "请重刷身份证!");
                return;
            }
            cardMsg = info;
            checkIsAppoint(info);//身份证数据的读取和上传进行进校离校的判断
            Log.i("infoss", "1111111111111111111111111");
            String text = info.getName() + "\n"
                    + info.getSexStr() + "\n"
                    + info.getNationStr() +"族"+ "\n"
                    + info.getBirthDate() + "\n"
                    + info.getIdCardNum() + "\n"
                    + info.getAddress() + "\n"
                    + info.getUsefulStartDate()+ " -- " + info.getUsefulEndDate() +"\n"
                    + info.getSignOffice() + "\n";


       //     System.out.println("识别的结果****************："+text);

/*
           Bitmap bitmap = BitmapFactory.decodeByteArray(info.getPortrait(), 0, info.getPortrait().length);
           test_img.setImageBitmap(bitmap);*/



        }
    };
}
