package com.wrs.gykjewm.baselibrary.utils.oss;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.wrs.gykjewm.baselibrary.base.BaseApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by VinsonYue on 2018/06/01.
 * Email: wldtk1012020811@163.com
 */

public class PictureUpload {
    private static PictureUpload pictureUpload;
    String stsServer = "https://wrs.gykjewm.com/appInterface/osstoken/main";
    private String endpoint = "oss-cn-hangzhou.aliyuncs.com";
    private String bucket = "guanyukeji-static";
    /**
     * 上传client
     */
    private OSS oss;
    /**
     * 上传次数
     */
    private int number;
    /**
     * 成功上传(本地文件名作为key,阿里云地址为value)
     */
    private Map<String, Object> success = new HashMap<>();
    /**
     * 失败上传(返回失败文件的本地地址)
     */
    private List<String> failure = new ArrayList<>();
    /**
     * 上传回调
     */
    private UploadFilesListener uploadFilesListener;

    /**
     * 上传回调
     */
    private UploadFileListener uploadFileListener;
    /**
     * 上传任务列表
     */
    private List<OSSAsyncTask> ossAsyncTasks = new ArrayList<>();
    private OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);

    /**
     * 单列模式
     *
     * @return
     */
    public static PictureUpload getInstance() {
        if (pictureUpload == null) {
            pictureUpload = new PictureUpload();
        }
        return pictureUpload;
    }

    /**
     * 构造函数
     */
    public PictureUpload() {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(9); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        oss = new OSSClient(BaseApplication.getInstance(), endpoint, credentialProvider, conf);
    }

    /**
     * 添加上传任务
     *
     * @param uploadFilePaths
     * @param listener
     */
    public void addTasks(String uploadPath, final List<String> uploadFilePaths, UploadFilesListener listener) {
        this.uploadFilesListener = listener;
        ossAsyncTasks.clear();
        for (String uploadFilePath : uploadFilePaths) {
            File uploadFile = new File(uploadFilePath);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = sdf.format(new Date());
            String fileName = uploadFile.getName();
            String endName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            /**
             * 阿里云上文件名称
             */
            String objectKey = uploadPath + File.separator + uuid + dateString + "." + endName;            /**
             * 用户自定义参数
             */
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.addUserMetadata("filePath", uploadFile.getPath());
            objectMetadata.addUserMetadata("fileName", uploadFile.getName());
            objectMetadata.addUserMetadata("objectKey", objectKey);


            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectKey, uploadFile.getPath(), objectMetadata);
            putObjectRequest.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest putObjectRequest, long currentSize, long totalSize) {
                }
            });
            /**
             * 上传任务
             */
            OSSAsyncTask task = oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    number++;
                    String ossUrl = getOSSFileUrl(putObjectRequest);
                    success.put(putObjectRequest.getMetadata().getUserMetadata().get("fileName"), ossUrl);
                    if (number == uploadFilePaths.size()) {
                        uploadFilesListener.onUploadComplete(success, failure);
                    }
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                    number++;
                    failure.add(putObjectRequest.getMetadata().getUserMetadata().get("filePath"));
                    if (number == uploadFilePaths.size()) {
                        uploadFilesListener.onUploadComplete(success, failure);
                    }
                }
            });
            /**
             * 添加到上传记录
             */
            ossAsyncTasks.add(task);
        }
    }

    /**
     * 添加上传任务
     *
     * @param uploadFilePath
     * @param listener
     */
    public void addTask(final Context context, String uploadPath, String uploadFilePath, UploadFileListener listener) {
        this.uploadFileListener = listener;
        ossAsyncTasks.clear();
        File uploadFile = new File(uploadFilePath);
        Log.d("lanzhu","uploadFile="+uploadFile.getAbsolutePath());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf.format(new Date());
        String fileName = uploadFile.getName();
        String endName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        /**
         * 阿里云上文件名称
         */
        String objectKey = uploadPath + File.separator + uuid + dateString + "." + endName;
        Log.d("lanzhu","objectKey="+objectKey);
        /**
         * 用户自定义参数
         */
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.addUserMetadata("filePath", uploadFile.getPath());
        objectMetadata.addUserMetadata("fileName", uploadFile.getName());
        objectMetadata.addUserMetadata("objectKey", objectKey);


        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, objectKey, uploadFile.getPath(), objectMetadata);

        /**
         * 上传任务
         */
        OSSAsyncTask task = oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                String ossUrl = getOSSFileUrl(putObjectRequest);
                uploadFileListener.onUploadSuccess(ossUrl);

            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                Log.d("lanzhu",e.getMessage());
                Log.d("lanzhu",e1.getMessage());
                String localFailureFilePath = putObjectRequest.getMetadata().getUserMetadata().get("filePath");
                uploadFileListener.onUploadFailure(localFailureFilePath);
            }
        });
        /**
         * 添加到上传记录
         */
        ossAsyncTasks.add(task);
    }

    public void cancleTasks() {
        for (OSSAsyncTask task : ossAsyncTasks) {
            if (!task.isCompleted()) {
                task.cancel();
            }
        }
    }

    private String getOSSFileUrl(PutObjectRequest putObjectRequest) {
        return "http://" + bucket + "." + endpoint + "/" + putObjectRequest.getObjectKey();
    }


}
