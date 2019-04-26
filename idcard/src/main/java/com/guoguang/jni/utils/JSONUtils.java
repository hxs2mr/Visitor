package com.guoguang.jni.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	
	public static boolean getJsonBoolean(JSONObject jsonObj, String name) {
		try {
			return jsonObj.getBoolean(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getJsonString(JSONObject jsonObj, String name) {
		try {
			return jsonObj.getString(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static int getJsonInt(JSONObject jsonObj, String name) {
		try {
			return jsonObj.getInt(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static double getJsonDouble(JSONObject jsonObj, String name) { 
		try {
			return jsonObj.getDouble(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return 0.0;
		}
	}

}
