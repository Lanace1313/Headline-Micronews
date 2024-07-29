package com.news.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.InputStream;

public class QiniuOssUtil {
    private static final String URL = "域名";
    private static final String ACCESS_KEY = "公钥";
    private static final String SECRET_KEY = "私钥";
    private static final String BUCKET_NAME = "OSS仓库";

    //上传文件,返回文件的公网访问地址
    public static String uploadFile(String fileName, InputStream file) throws QiniuException {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        UploadManager uploadManager = new UploadManager(new Configuration(Region.huanan()));
        String token = auth.uploadToken(BUCKET_NAME);
        fileName = "myimage/" + fileName;
        Response res = uploadManager.put(file, fileName, token, null, null);
        if (!res.isOK()) {
            throw new RuntimeException("上传七牛云出错:" + res);
        }
        return URL + "/" + fileName;
    }
}
