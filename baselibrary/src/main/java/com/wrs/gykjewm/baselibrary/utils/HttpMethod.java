package com.wrs.gykjewm.baselibrary.utils;




import com.wrs.gykjewm.baselibrary.base.BaseApplication;
import com.wrs.gykjewm.baselibrary.utils.interceptor.AddCookiesInterceptor;
import com.wrs.gykjewm.baselibrary.utils.interceptor.LogInterceptor;
import com.wrs.gykjewm.baselibrary.utils.interceptor.ReceivedCookiesInterceptor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * description:
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午1:47
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class HttpMethod {
    private int HTTP_CONNECT_TIMEOUT = 60;
    private int HTTP_WRITE_TIMEOUT = 60;
    private int HTTP_READ_TIMEOUT = 100;

    private OkHttpClient okHttpClient;

    ACache mACache = ACache.get(BaseApplication.getInstance());
    private AddCookiesInterceptor mAddCookiesInterceptor = new AddCookiesInterceptor(mACache,-1);
    private ReceivedCookiesInterceptor mReceivedCookiesInterceptor = new ReceivedCookiesInterceptor(mACache,-1);

    private LogInterceptor logInterceptor = new LogInterceptor();

    public static final HttpMethod instance = new HttpMethod();

    private HttpMethod(){

        // Log信息
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 公私密匙
        //MarvelSigningInterceptor signingInterceptor = new MarvelSigningInterceptor(
        //        BuildConfig.MARVEL_PUBLIC_KEY, BuildConfig.MARVEL_PRIVATE_KEY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(mAddCookiesInterceptor);
        builder.addInterceptor(mReceivedCookiesInterceptor);
        builder.addInterceptor(logInterceptor);
        builder.addInterceptor(loggingInterceptor);
        okHttpClient = builder.build();
    }

    public OkHttpClient getClient(int serviceId){
        mAddCookiesInterceptor.setServiceId(serviceId);
        mReceivedCookiesInterceptor.setServiceId(serviceId);
        HashMap<Integer,String> cookies =  (HashMap<Integer, String>) mACache.getAsObject("COOKIES");
        if(null == cookies){
            cookies = new HashMap<Integer,String>();
        }
        mReceivedCookiesInterceptor.setCookies(cookies);
        return okHttpClient;
    }

    public OkHttpClient getNewClient(int serviceId){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS);

        HashMap<Integer,String> cookies =  (HashMap<Integer, String>) ACache.get(BaseApplication.getInstance()).getAsObject("COOKIES");
        if(null == cookies){
            cookies = new HashMap<Integer,String>();
        }
        AddCookiesInterceptor addCookiesInterceptor = new AddCookiesInterceptor(mACache,-1);
        ReceivedCookiesInterceptor receivedCookiesInterceptor = new ReceivedCookiesInterceptor(mACache,-1);

        addCookiesInterceptor.setServiceId(serviceId);
        receivedCookiesInterceptor.setServiceId(serviceId);
        receivedCookiesInterceptor.setCookies(cookies);

        builder.addInterceptor(addCookiesInterceptor);
        builder.addInterceptor(receivedCookiesInterceptor);
        return builder.build();
    }
}
