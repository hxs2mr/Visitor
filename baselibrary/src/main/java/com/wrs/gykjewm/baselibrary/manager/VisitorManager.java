package com.wrs.gykjewm.baselibrary.manager;



import com.wrs.gykjewm.baselibrary.base.BaseApplication;
import com.wrs.gykjewm.baselibrary.common.Constant;
import com.wrs.gykjewm.baselibrary.domain.Account;
import com.wrs.gykjewm.baselibrary.utils.ACache;

/**
 * desc   : 访客系统缓存文件管理器
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/11/2010:38
 * version: 1.0
 */
public class VisitorManager {

    private ACache mACache;

    private VisitorManager(){
        this.mACache = ACache.get(BaseApplication.getInstance());
    }

    public static VisitorManager getManager(){
        return VisitorManager.VisitorManagerHolder.instance;
    }



    public void saveAccount(Account account){
        mACache.put(Account.class.getSimpleName(),account);
    }

    public Account getAccount(){
        return (Account) mACache.getAsObject(com.wrs.gykjewm.baselibrary.domain.Account.class.getSimpleName());
    }


    private static class VisitorManagerHolder{
        private static VisitorManager instance = new VisitorManager();
    }

    public void saveToken(String token){
        mACache.put(Constant.TOKEN,token);
    }

    public String getToken(){
        return mACache.getAsString(Constant.TOKEN);
    }

    public void saveSchoolId(String schoolId){
        mACache.put(Constant.SCHOOL_ID,schoolId);
    }

    public String getSchoolName(){
        return mACache.getAsString(Constant.SCHOOL_NAME);
    }


    public void saveSchoolName(String schoolName){
        mACache.put(Constant.SCHOOL_NAME,schoolName);
    }

    public String getSchoolId(){
        return mACache.getAsString(Constant.SCHOOL_ID);
    }



    public void saveDeviceId(String deviceId){
        mACache.put(Constant.DEVICEID,deviceId);
    }

    public String getDeviceId(){
        return mACache.getAsString(Constant.DEVICEID);
    }


    public void saveDeviceName(String deviceName){
        mACache.put(Constant.DEVICENAME,deviceName);
    }

    public String getDeviceName(){
        return mACache.getAsString(Constant.DEVICENAME);
    }


    public void clearAllData(){
        mACache.remove(Constant.TOKEN);
        mACache.remove(Account.class.getSimpleName());
        mACache.remove(Constant.DEVICEID);
        mACache.remove(Constant.DEVICENAME);
        mACache.remove(Constant.SCHOOL_NAME);
    }


    public void clearDeviceId(){
        mACache.remove(Constant.DEVICEID);
    }
    public void clearDeviceName(){
        mACache.remove(Constant.DEVICENAME);
    }
}
