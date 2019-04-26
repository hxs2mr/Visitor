package com.guoguang.jni.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import com.guoguang.jni.idcard.IDCardManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 
 * @author zjxin2 on 2016-9-22
 *
 */
public class DeviceInfo {
	private final String TAG = "HardwareCheck";

	private SharedPreferences mSpf;
	private Context mContext;
	
	public DeviceInfo(Context context) {
		mContext = context;
		mSpf = context.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
		
	}
	//判断是否有网络
	public boolean isNetworkConnected(Context context) {  
	     if (context != null) {  
	         ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                 .getSystemService(Context.CONNECTIVITY_SERVICE);  
	         @SuppressLint("MissingPermission") NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
	         if (mNetworkInfo != null) {  
	             return mNetworkInfo.isAvailable();  
	         }  
	     }  
	     return false;  
	 }
	/**
	 * 获取用户登录密码
	 * @return
	 */
	public String getPassword() {
		return mSpf.getString("password", "");
	}
	
	/**
	 * 保存用户登录密码
	 */
	public void savePassword(String password) {
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("password", password);
		editor.commit();
	}
	
	
	/**
	 * 获取用户登录ID
	 * @return
	 */
	public String getUserId() {
		return mSpf.getString("user_id", "");
	}
	
	/**
	 * 保存用户登录ID
	 * @param userId
	 */
	public void saveUserId(String userId) {
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("user_id", userId);
		editor.commit();
	}
	
	/***
	 * 服务主机端口
	 * @param shport
	 */
	public void setServiceHostPort(String shport){
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("shport", shport);
		editor.commit();
	}
	/**
	 * 获取服务主机端口
	 * @return
	 */
	public String getServiceHostPort() {
		return mSpf.getString("shport", "8080");
	}
	/***
	 * 服务主机IP
	 * @param ship
	 */
	public void setServiceHostIp(String ship){
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("ship", ship);
		editor.commit();
	}
	/**
	 * 获取服务主机IP
	 * @return
	 */
	public String getServiceHostIp() {
		return mSpf.getString("ship", "10.252.252.244");	/// 192.168.43.224	 10.1.53.46	10.252.252.244
	}
	/**
	 * 本地主机IP
	 */
	public void setLocalHostIp(String lhip){
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("lhip", lhip);
		editor.commit();
	}
	/**
	 * 获取本地主机IP
	 * @return
	 */
	public String getLocalHostIp() {
		return mSpf.getString("lhip", "0.0.0.0");
	}
	/**
	 * 设备名称
	 * @param epname
	 */
	public void setEquipmentName(String epname){
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("epname", epname);
		editor.commit();
	}
	/**
	 * 获取设备名称
	 * @return
	 */
	public String getEquipmentName() {
		return mSpf.getString("epname", "人证合一系统");
	}
	/**
	 * 设备编号
	 * @param epnumber
	 */
	public void setEquipmentNumber(String epnumber){
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("epnumber", epnumber);
		editor.commit();
	}
	/**
	 * 获取设备编号
	 * @return
	 */
	public String getEquipmentNumber() {
		return mSpf.getString("epnumber", "GRG-123456");
	}
	/**
	 * 场所名称
	 * @param sitename
	 */
	public void setSiteName(String sitename){
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putString("sitename", sitename);
		editor.commit();
	}
	/**
	 * 获取场所名称
	 * @return
	 */
	public String getSiteName() {
		return mSpf.getString("sitename", "GRGRenzhengheyantech");
	}
	/**
	 * 更新红外设置状态
	 * @param state
	 */
	public void updateRedLightState(boolean state) {
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putBoolean("redlight", state);
		editor.commit();
	}

	/**
	 * 可红外是否可用
	 * @return
	 */
	public boolean isRedLightEnable() {
		return mSpf.getBoolean("redlight", true);
	}
	
	/**
	 * 更新可见光设置状态
	 * @param state
	 */
	public void updateVisibleLightState(boolean state) {
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putBoolean("visiblelight", state);
		editor.commit();
	}
	
	/**
	 * 可见光是否可用
	 * @return
	 */
	public boolean isVisibleLightEnable() {
		return mSpf.getBoolean("visiblelight", true);
	}
	
	/**
	 * 保存阈值
	 * @param threshold
	 */
	public void saveThreshold(float threshold) {
		SharedPreferences.Editor editor = mSpf.edit();
		editor.putFloat("similarity", threshold);
		editor.commit();
	}
	
	/**
	 * 获取阈值
	 * @return
	 */
	public float getThreshold() {
		return mSpf.getFloat("similarity", (float)0.40);
	}
	
	
	/**
	 * 通过该WIFI获取mac地址
	 * @return
	 */
	public String getMacViaWifi() {
		WifiManager wifi = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	/**
	 * 通过linux命令获取mac地址
	 * @return
	 */
	public String getMacViaCmd() 
	{
	    String macSerial = "";
	    String str = "";

	    try 
	    {
	        Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
	        InputStreamReader ir = new InputStreamReader(pp.getInputStream());
	        LineNumberReader input = new LineNumberReader(ir);

	        for (; null != str;) 
	        {
	            str = input.readLine();
	            if (str != null)
	            {
	                macSerial = str.trim();// 去空格
	                break;
	            }
	        }
	    } catch (IOException ex) {
	        // 赋予默认值
	        ex.printStackTrace();
	    }
	    return macSerial;
	}
	//获取本地IP
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}

		return "";
	}
	
	

	/**
	 * Check network state
	 * 
	 * @return
	 */
	public boolean isNetworkAvail() {
		if (mContext != null) {
			ConnectivityManager cm = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Check IDCard model state
	 * 
	 * @param activity
	 * @return
	 */
	public boolean isIDCardModelOk(Activity activity) {
		try {
			return (new IDCardManager(activity)).isSAMStatusInNormal();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Check Camera state
	 * 
	 * @param camId
	 * @return
	 */
	@SuppressLint("NewApi")
	public boolean isCameraOk(int camId) {
		Camera camera;
		try {
			camera = Camera.open(camId);
		} catch (Exception e) {
			return false;
		}

		if (camera != null) {
			camera.release();
		}

		return true;
	}
}
