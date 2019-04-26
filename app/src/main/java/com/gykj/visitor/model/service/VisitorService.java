package com.gykj.visitor.model.service;

import com.gykj.visitor.entity.LoginEntity;
import com.gykj.visitor.entity.ParentEntity;
import com.gykj.visitor.entity.PollEntity;
import com.gykj.visitor.entity.TeacherEntity;
import com.gykj.visitor.entity.TokenEntity;
import com.gykj.visitor.entity.VisitorCountEntity;
import com.gykj.visitor.entity.VisitorEntity;
import com.gykj.visitor.entity.VisitorListEntity;
import com.wrs.gykjewm.baselibrary.domain.BaseResult;
import com.wrs.gykjewm.baselibrary.domain.NullEntity;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * desc   : 后台接口
 * author : josh.lu
 * e-mail : 1113799552@qq.com
 * date   : 2018/9/209:44
 * version: 1.0
 */
public interface VisitorService {
    /**
     * 检查是否预约
     * @param body
     * @return
     */
    @POST("visitor/checkIsAppoint")
    Observable<BaseResult<VisitorEntity>> checkIsAppoint(@Body RequestBody body);

    /**
     * 搜索教师
     * @param body
     * @return
     */
    @POST("teacher/search")
    Observable<BaseResult<List<TeacherEntity>>> teacherSearch(@Body RequestBody body);

    /**
     * 获取家长信息
     * @param parentId
     * @return
     */
    @GET("parent/info/{parentId}")
    Observable<BaseResult<ParentEntity>> getParentInfo(@Path("parentId") long parentId);

    /**
     * 社会人士未预约登记
     * @param body
     * @return
     */
    @POST("visitor/appointPolling")
    Observable<BaseResult<NullEntity>> appointPolling(@Body RequestBody body);

    /**
     * 家长扫码登记
     * @param body
     * @return
     */
    @POST("visitor/parentPolling")
    Observable<BaseResult<PollEntity>> parentPolling(@Body RequestBody body);

    /**
     * 查询未离校人员人数
     * @return
     */
    @GET("visitor/noLeavingCount")
    Observable<BaseResult<VisitorCountEntity>> noLeavingCount();

    /**
     * 获取未离校人员列表
      * @param body
     * @return
     */
    @POST("visitor/noLeavingList")
    Observable<BaseResult<List<VisitorListEntity>>> noLeavingList(@Body RequestBody body);

    /**
     * 手动确认离校状态
     * @param id
     * @return
     */
    @GET("visitor/changeStatus/{id}")
    Observable<BaseResult<NullEntity>> changeStatus(@Path("id") long id);

    /**
     * 账号密码登录接口
     * @param body
     * @return
     */
    @POST("user/login")
    Observable<LoginEntity> login(@Body RequestBody body);


    /**获取有效Token接口
     * @param deviceId
     * @return
     */
    @GET("user/getToken/{deviceId}")
    Observable<BaseResult<TokenEntity>> getToken(@Path("deviceId") String deviceId);

    /**
     * 图片上传
     * @param body
     * @return
     */
    @POST("visitor/uplodeImg")
    Observable<BaseResult<NullEntity>> uplodeImg(@Body RequestBody body);

    /**
     * 社会人士确认进入
     * @param id
     * @return
     */
    @GET("visitor/confirmEntry/{id}")
    Observable<BaseResult<NullEntity>> confirmEntry(@Path("id") long id);

}
