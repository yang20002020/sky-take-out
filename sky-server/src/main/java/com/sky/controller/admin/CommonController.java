package com.sky.controller.admin;

import com.aliyuncs.exceptions.ClientException;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags="通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String >upload(MultipartFile file){
        log.info("文件上传：{}",file);

        try{
            //原始文件名   举例  originalFilename= “wang.png”
            String originalFilename= file.getOriginalFilename();
            //截取原始文件名的后缀 dff.png  extension=".png"
            String extension=originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;
            //文件的请求路径
            // filepath:  https://tlisa-web.oss-cn-beijing.aliyuncs.com/30a1c136-e574-49b8-ac8e-808a4c966ae4.png
            String filepath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filepath);
        }catch(IOException | ClientException e){
            e.printStackTrace();
            log.error("文件上传失败：{}",e);
        }
        return null;
    }

}
