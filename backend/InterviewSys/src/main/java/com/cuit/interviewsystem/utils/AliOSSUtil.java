package com.cuit.interviewsystem.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.cuit.interviewsystem.config.AliOSSConfig;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class AliOSSUtil {
    @Resource
    private OSS ossClient;
    @Resource
    private AliOSSConfig aliOSSConfig;

    /**
     * 上传文件, 若file为空则返回null
     * @param file 需上传的文件
     * @param prefix 文件名前缀
     * @return 上传后的文件地址
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String prefix) throws IOException { {
        if (file == null) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = prefix + UUID.randomUUID().toString().replace("-", "");
//        file.transferTo(new File("../../templates" + newFileName + suffix));
        //获取文件的后缀，产生以为不重复的名称
        //调用阿里云的SDK上传至OSS
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliOSSConfig.getBucketName(), newFileName + suffix,
                file.getInputStream());

        // 上传文件。
        PutObjectResult result = ossClient.putObject(putObjectRequest);
        return "https://" + aliOSSConfig.getBucketName() + "." + aliOSSConfig.getEndpoint() + "/" + newFileName + suffix;
    }}
}
