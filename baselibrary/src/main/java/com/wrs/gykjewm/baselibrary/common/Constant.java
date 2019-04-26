package com.wrs.gykjewm.baselibrary.common;

import com.arcsoft.face.FaceFeature;
/**
 * description:
 * <p>
 * author: josh.lu
 * created: 22/7/18 下午8:23
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class Constant {
  //  public final static String SERVER_ADDRESS = "http://autotest.guanyukj.com/";
   public final static String SERVER_ADDRESS = "http://autotest.guanyukj.com/";
    //public final static String SERVER_ADDRESS = "http://fk.guanyukj.com/"; //正式服
    //public final static String SERVER_ADDRESS = "http://autotest.guanyukj.com/"; //测试服
    public static final int VID = 1024;
    public static final int PID = 50010;
    public static final String ID_CARD_INFO = "ID_CARD_INFO";
    public static final String VISITOR_ID = "VISITOR_ID";
    public static final String VISITOR = "VISITOR";
    public static final String PARENT_INFO = "PARENT_INFO";
    public static final String ACTION_GET_VISITOR_LIST = "ACTION_GET_VISITOR_LIST";
    public static final String VISITOR_NUMBER = "VISITOR_NUMBER";


    public static final String DEVICENAME = "DEVICENAME";
    public static final String TOKEN = "TOKEN";
    public static final String DEVICEID = "DEVICEID";
    public static final String SCHOOL_ID = "SCHOOL_ID";

    public static final String FACE_BITMAP = "FACE_BITMAP";
    public static FaceFeature[]  FACEFEATURES;

    public static final String VISITOR_TYPE = "VISITOR_TYPE";
    public static final String AUTH_TYPE = "AUTH_TYPE";
    public static final int IDCARD = 1;
    public static final int QRCODE = 2;


    public static final String OSS_PATH = "visitor/qrcode/pic";
    public static final String SCHOOL_NAME = "SCHOOL_NAME";
}
