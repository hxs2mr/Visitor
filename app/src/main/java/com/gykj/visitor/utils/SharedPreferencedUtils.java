package com.gykj.visitor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.gykj.acface.common.Constants;
import com.wrs.gykjewm.baselibrary.base.BaseApplication;


public class SharedPreferencedUtils {
    public static SharedPreferences mPreference;

    public static SharedPreferences getPreference() {
        if (mPreference == null)
            mPreference = BaseApplication.getInstance().getSharedPreferences(Constants.PROJECT_NAME,Context.MODE_PRIVATE);
        return mPreference;
    }
        
    public static void setInt( String name, int value) {
        getPreference().edit().putInt(name, value).commit();
    }

    public static int getInt( String name, int default_i) {
        return getPreference().getInt(name, default_i);
    }


    public static void setStr( String name, String value) {
        getPreference().edit().putString(name, value).commit();
    }

    public static String getStr( String name) {
        return getPreference().getString(name, null);
    }

    public static String getStr( String name, String defalt) {
        return getPreference().getString(name, defalt);
    }


    public static boolean getBool( String name,
                                     boolean defaultValue) {
        return getPreference().getBoolean(name, defaultValue);
    }


    public static void setBool( String name, boolean value) {
        getPreference().edit().putBoolean(name, value).commit();
    }

    public static void setFloat(Context context, String name, Float value) {
        getPreference().edit().putFloat(name, value).commit();
    }

    public static Float getFloat( String name, Float value) {
        return getPreference().getFloat(name, 0);
    }

    public static void setLong( String name, Long value) {
        getPreference().edit().putLong(name, value).commit();
    }

    public static Long getLong( String name, Long defaultValue) {
        return getPreference().getLong(name, defaultValue);
    }


}
