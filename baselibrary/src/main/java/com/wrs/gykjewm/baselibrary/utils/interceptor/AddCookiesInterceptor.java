package com.wrs.gykjewm.baselibrary.utils.interceptor;






import com.wrs.gykjewm.baselibrary.manager.VisitorManager;
import com.wrs.gykjewm.baselibrary.utils.ACache;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * description:
 * <p>
 * author: josh.lu
 * created: 11/6/18 下午12:46
 * email:  1113799552@qq.com
 * version: v1.0
 */
public class AddCookiesInterceptor implements Interceptor{

    private ACache mACache;
    private int mServiceId;

    public AddCookiesInterceptor(ACache aCache, int serviceId) {
        this.mACache = aCache;
        this.mServiceId = serviceId;
    }

    public void setServiceId(int serviceId){
        this.mServiceId = serviceId;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String cookie = getCookie();
        if(null != cookie && !cookie.equals(""))
            builder.addHeader("Cookie", getCookie());
        builder.addHeader("Content-Type", "multipart/form-data");
        builder.addHeader("Accept", "application/json");
        if(null != VisitorManager.getManager().getToken()){
            builder.addHeader("TOKEN", VisitorManager.getManager().getToken());
        }
        return chain.proceed(builder.build());
    }

    private String getCookie(){
        String cookie = null;
        if(mServiceId == 0xFF){
            return  null;
        }
        HashMap<Integer,String> cookies = (HashMap<Integer, String>)mACache.getAsObject("COOKIES");
        if(null != cookies)
            cookie = cookies.get(mServiceId);
        return cookie;
    }

}
