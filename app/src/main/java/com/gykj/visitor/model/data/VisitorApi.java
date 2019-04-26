package com.gykj.visitor.model.data;

import android.support.v4.util.ArrayMap;

import com.gykj.visitor.entity.LoginEntity;
import com.gykj.visitor.entity.ParentEntity;
import com.gykj.visitor.entity.PollEntity;
import com.gykj.visitor.entity.TeacherEntity;
import com.gykj.visitor.entity.TokenEntity;
import com.gykj.visitor.entity.VisitorCountEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.entity.VisitorListEntity;
import com.gykj.visitor.model.service.VisitorService;
import com.wrs.gykjewm.baselibrary.common.Constant;
import com.wrs.gykjewm.baselibrary.domain.BaseResult;
import com.wrs.gykjewm.baselibrary.domain.NullEntity;
import com.wrs.gykjewm.baselibrary.utils.HttpMethod;
import com.wrs.gykjewm.baselibrary.utils.JsonUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * desc   :
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/209:50
 * version: 1.0
 */
public class VisitorApi {

    private String BASE_URL = Constant.SERVER_ADDRESS;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/json; charset=utf-8");

    private VisitorService mService;
    private Retrofit retrofit;
    private OkHttpClient client;

    private VisitorApi(){
        retrofit = new Retrofit.Builder()
                .client(HttpMethod.instance.getClient(0xF0))
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = retrofit.create(VisitorService.class);
        buildHttpClient();
    }

    private static class VisitorHolder{
        private static VisitorApi instance = new VisitorApi();
    }

    public static VisitorApi getApi(){
        return VisitorHolder.instance;
    }


    private void buildHttpClient(){
        this.client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        return response;
                    }
                })
                .build();
    }


    /**
     * 检查访客是否预约
     * @param address
     * @param birthDate
     * @param effectiveDate
     * @param idCard
     * @param nation
     * @param signOrgina
     * @param visitName
     * @param visitSex
     * @return
     */
    public Observable<BaseResult<VisitorEntity>> checkIsAppoint(String address, String birthDate, String effectiveDate, String idCard,
                                                                String nation, String signOrgina, String visitName, int visitSex){
        Map<String,Object> map = new ArrayMap<>();
        map.put("address",address);
        map.put("birthDate",birthDate);
        map.put("effectiveDate",effectiveDate);
        map.put("idCard",idCard);
        map.put("nation",nation);
        map.put("signOrgina",signOrgina);
        map.put("visitName",visitName);
        map.put("visitSex",visitSex);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, JsonUtils.map2json(map));
        return mService.checkIsAppoint(body);
    }

    /**
     * 搜索老师
     * @param patten
     * @return
     */
    public Observable<BaseResult<List<TeacherEntity>>> teacherSearch(String patten){
        Map<String,Object> map = new ArrayMap<>();
        map.put("patten",patten);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, JsonUtils.map2json(map));
        return mService.teacherSearch(body);
    }

    /**
     * 获取家长信息
     * @param parentId
     * @return
     */
    public Observable<BaseResult<ParentEntity>> getParentInfo(long parentId){
        return mService.getParentInfo(parentId);
    }


    /**
     * 社会人士未预约登记
     * @param teacherId
     * @param visitId
     * @param visitObjectiv
     * @param visitPhone
     * @param visitorName
     * @return
     */
    public Observable<BaseResult<NullEntity>> appointPolling(long teacherId,long visitId,String visitObjectiv,String visitPhone,String visitorName){
        Map<String,Object> map = new ArrayMap<>();
        map.put("teacherId",teacherId);
        map.put("visitId",visitId);
        map.put("visitObjectiv",visitObjectiv);
        map.put("visitPhone",visitPhone);
        map.put("visitorName",visitorName);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, JsonUtils.map2json(map));
        return mService.appointPolling(body);
    }


    /**
     * 家长扫码登记
     * @param parentId
     * @param visitObjectiv
     * @return
     */
    public Observable<BaseResult<PollEntity>> parentPolling(long parentId, String parentName, String visitObjectiv){
        Map<String,Object> map = new ArrayMap<>();
        map.put("parentId",parentId);
        map.put("parentName",parentName);
        map.put("visitObjectiv",visitObjectiv);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, JsonUtils.map2json(map));
        return mService.parentPolling(body);
    }

    /**
     * 查询未离校人员数
     * @return
     */
    public Observable<BaseResult<VisitorCountEntity>> noLeavingCount(){
        return mService.noLeavingCount();
    }


    /**
     * 获取未离校人员列表
     * @param offset
     * @return
     */
    public Observable<BaseResult<List<VisitorListEntity>>> noLeavingList(int offset){
        Map<String,Object> map = new ArrayMap<>();
        map.put("offset",offset);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, JsonUtils.map2json(map));
        return mService.noLeavingList(body);
    }

    /**
     * 手动更改离校状态
     * @param id
     * @return
     */
    public Observable<BaseResult<NullEntity>> changeStatus(long id){
        return mService.changeStatus(id);
    }


    /**
     * 账号密码登录接口
     * @param account
     * @param pass
     * @return
     */
    public Observable<LoginEntity> login(String account,String pass){
        Map<String,Object> map = new ArrayMap<>();
        map.put("account",account);
        map.put("pass",pass);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, JsonUtils.map2json(map));
        return mService.login(body);
    }


    /**
     * 获取有效Token
     * @param deviceId
     * @return
     */
    public Observable<BaseResult<TokenEntity>> getToken(String deviceId){
        return mService.getToken(deviceId);
    }


    /**
     * 图片上传
     * @param id
     * @param imgUrl
     * @return
     */
    public Observable<BaseResult<NullEntity>> uplodeImg(long id,String imgUrl){
        Map<String,Object> map = new ArrayMap<>();
        map.put("id",id);
        map.put("imgUrl",imgUrl);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, JsonUtils.map2json(map));
        return mService.uplodeImg(body);
    }


    /**
     * 社会人士确认进入
     * @param id
     * @return
     */
    public Observable<BaseResult<NullEntity>> confirmEntry(long id){
        return mService.confirmEntry(id);
    }

}
