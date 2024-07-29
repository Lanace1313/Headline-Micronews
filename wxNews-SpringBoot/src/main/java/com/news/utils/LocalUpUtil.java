package com.news.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class LocalUpUtil {
    // 文件本地存储目录
    private static final String STORAGE_DIR = "D:/Download/Edge/image/";
    //虚拟路径映射
    private static final String VIRTUAL_PATH = "http://localhost:8080/image/";

    // 上传文件，返回文件的访问地址
    public static String uploadFile(String fileName, InputStream file) {
        // 创建目标文件的完整路径
        File targetFile = new File(STORAGE_DIR + fileName);

        // 将输入流写入文件
        try (FileOutputStream out = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = file.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            // 释放资源
            buffer.clone();
            // 返回文件的访问地址
            return VIRTUAL_PATH + fileName;
        } catch (IOException e) {
            log.error("上传文件失败", e);
            return null; // 返回空值
        }
    }
}