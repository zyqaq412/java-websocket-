package com.hzy.webserver.service;

import com.hzy.webserver.utils.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: FIleService
 * @Author zxwyhzy
 * @Date: 2023/5/16 13:15
 * @Version 1.0
 */
public interface FileService {

    Result uploadImg(MultipartFile img);
}
