package com.sky.config;
import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类： 用于创建另一个模块中的AliOSSUtil对象
 *  目的：初始化 AliOSSUtil
 */

@Configuration
@Slf4j
public class OssConfiguration {


    @Bean
    @ConditionalOnMissingClass
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象:{}",aliOssProperties);
      return  new AliOssUtil(aliOssProperties.getEndpoint(), aliOssProperties.getBucketName());
    }
}
