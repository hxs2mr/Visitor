package com.wrs.gykjewm.baselibrary.utils.oss;

import java.util.List;
import java.util.Map;

/**
 * Created by VinsonYue on 2018/06/01.
 * Email: wldtk1012020811@163.com
 */

public interface UploadFilesListener {
    /**
     * 上传完成
     *
     * @param success
     * @param failure
     */
    void onUploadComplete(Map<String, Object> success, List<String> failure);
}
