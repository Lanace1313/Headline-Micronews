package com.news.controller;

import com.news.utils.LocalUpUtil;
import com.news.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class FileUploadController {
    // 上传文件
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        // 获取文件名称
        String originalFilename = file.getOriginalFilename();
        // 判断文件名
        if (originalFilename == null || originalFilename.trim().isEmpty() || originalFilename.contains("..")) {
            return Result.error("文件名无效");
        }

        // 生成唯一文件名并保存至静态资源目录
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        /*
        //七牛云构建访问URL
        String fileUrl = QiniuOssUtil.uploadFile(fileName, file.getInputStream());
        */
        //本地构建访问URL
        String fileUrl = LocalUpUtil.uploadFile(fileName, file.getInputStream());
        if (fileUrl == null) return Result.error("本地上传失败");

        return Result.ok(fileUrl);
    }
}
