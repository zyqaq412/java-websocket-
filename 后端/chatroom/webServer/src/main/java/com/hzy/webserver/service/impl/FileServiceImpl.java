package com.hzy.webserver.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.hzy.webserver.service.FileService;
import com.hzy.webserver.utils.PathUtils;
import com.hzy.webserver.utils.Result;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @title: FileServiceImpl
 * @Author zxwyhzy
 * @Date: 2023/5/16 13:16
 * @Version 1.0
 */
@Service
@Data
@ConfigurationProperties(prefix = "oss")
public class FileServiceImpl implements FileService {


    String endpoint;
    String keyid;
    String keysecret;
    String bucketname;

    @Override
    public Result uploadImg(MultipartFile img) {

        String originalFilename = img.getOriginalFilename();
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = upload(img, filePath);//  2099/2/3/wqeqeqe.png
        System.out.println(url);
        return Result.ResultOk(url);
    }

    @Override
    public Result uploadFile(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        fileName = fileName.replaceAll(" ", "")
                // 文件名中的+号一定要替换
                /* 不然会报这个错
                    <Code>NoSuchKey</Code>
                  <Message>The specified key does not exist.</Message>
                 */
                .replaceAll("\\+","-");
        // String fileName = file.getOriginalFilename();
        //生成随机唯一值，使用uuid，添加到文件名称里面，不会导致重名
        String uuid = UUID.randomUUID()
                .toString()
                .replaceAll("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        fileName = datePath + uuid + "-" + fileName;
        String url = upload(multipartFile, fileName);
        return Result.ResultOk(url);
    }

    public String upload(MultipartFile file, String filePath) {

        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
            // 上传文件流。
            InputStream inputStream = file.getInputStream();
            // String fileName = file.getOriginalFilename();
            //生成随机唯一值，使用uuid，添加到文件名称里面，不会导致重名
            // String uuid = UUID.randomUUID().toString().replaceAll("-","");
            // fileName = uuid+fileName;
            //调用方法实现上传
            ossClient.putObject(bucketname, filePath, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            //上传之后文件路径
            // https://yygh-cccwm.oss-cn-shenzhen.aliyuncs.com/01.jpg
            String url = "https://" + bucketname + "." + endpoint + "/" + filePath;
            //返回
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
