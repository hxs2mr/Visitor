package com.guoguang.jni.idcard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.guoguang.jni.utils.FileUtils;


public class IDCardRecognition extends Thread {
	private String TAG = getClass().getSimpleName();
	private Activity mContext;
	private boolean running = false;
	private IDCardManager mIDCardManager;
//	private String mIdCardNo = "";
	private FileUtils mFileUtils;

	private boolean isCardFound = false;
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 1) {
				mIDCardRecListener.onResp((IDCardMsg)msg.obj);
			} else {
				mIDCardRecListener.onResp(null);
			}
		};
	};
	
	public IDCardRecognition(Activity context, IDCardRecListener listener) {
		mContext = context;
		mIDCardRecListener = listener;
		running = true;
		
		mFileUtils = new FileUtils();
	}
	
	public void close() {
		running = false;
	}

	@Override
	public void run() {
		super.run();
		Looper.prepare();
		while (this.running) {
			//mHandler.sendEmptyMessage(2);
			if(mIDCardManager == null) {
				init();
				sleepTime(1000);
			} else {
				int ret = mIDCardManager.SDT_GetSAMStatus();
				if(ret != 0X90) {
					init();
					sleepTime(100);
				}
				try {
					IDCardMsg msg = mIDCardManager.getIDCardMsg();
//					if(!isCardFound) {
					if(msg!=null){
						isCardFound = true;
						mFileUtils.saveCardImg(msg.getPortrait());
						mHandler.sendMessage(mHandler.obtainMessage(1, msg));
					}else{
						mHandler.sendEmptyMessage(2);
					}
				} catch (IDCardException e) {
					isCardFound = false;
//					GrgLog.w(TAG, "IDCardException", e);
				}
//				GrgLog.w(TAG, "ret = " + ret + ", isCardFound = " + isCardFound);
				sleepTime(100);
			}
		}
	}
	private void init() {
		try {
			mIDCardManager = new IDCardManager(mContext);
			mIDCardManager.SDT_ResetSAM();
			Log.i(TAG, "init IDCardManager");
		} catch (Exception e) {
			Log.w(TAG, "IDCardRecognition", e);
		}
	}
	
	private void sleepTime(long time) {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	
	private IDCardRecListener mIDCardRecListener;
	
	public interface IDCardRecListener {
		public void onResp(IDCardMsg info);
	}

}
