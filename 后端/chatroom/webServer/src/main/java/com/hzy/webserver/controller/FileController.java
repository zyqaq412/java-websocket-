package com.hzy.webserver.controller;

import com.hzy.webserver.service.FileService;
import com.hzy.webserver.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: FileController
 * @Author zxwyhzy
 * @Date: 2023/5/16 13:15
 * @Version 1.0
 */
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result uploadImg(@RequestParam("img") MultipartFile multipartFile) {
        System.out.println("上传图片");
        return fileService.uploadImg(multipartFile);

    }
    @PostMapping("/upload/file")
    public Result uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        System.out.println("上传文件");
        return fileService.uploadFile(multipartFile);
    }
}
