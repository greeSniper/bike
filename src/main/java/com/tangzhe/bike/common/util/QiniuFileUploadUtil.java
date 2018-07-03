package com.tangzhe.bike.common.util;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.tangzhe.bike.common.constants.Constants;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by 唐哲
 * 2018-06-08 14:56
 * 七牛云图片上传工具类
 */
public class QiniuFileUploadUtil {

    private QiniuFileUploadUtil() {}

    private static QiniuFileUploadUtil instance;

    public static QiniuFileUploadUtil getInstance() {
        if(instance == null) {
            instance = new QiniuFileUploadUtil();
        }
        return instance;
    }

    public String uploadHeadImg(MultipartFile file) throws IOException {
        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(Constants.QINIU_ACCESS_KEY, Constants.QINIU_SECRET_KEY);
        String upToken = auth.uploadToken(Constants.QINIU_HEAD_IMG_BUCKET_NAME);
        Response response = uploadManager.put(file.getBytes(),null, upToken);
        // 解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        return putRet.key;
    }

}
