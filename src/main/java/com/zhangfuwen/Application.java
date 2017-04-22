package com.zhangfuwen;

import com.zhangfuwen.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;

@SpringBootApplication
//        (
//        scanBasePackages = {
//                "com.zhangfuwen.models",
//                "com.zhangfuwen.models.user",
//                "com.zhangfuwen.repositories",
//                "com.zhangfuwen.repositories.user",
//                "com.zhangfuwen.services",
//                "com.zhangfuwen.services.user",
//                "com.zhangfuwen.storage",
//                "com.zhangfuwen.validators",
//                "com.zhangfuwen.webapp.devices",
//                "com.zhangfuwen.webapp.user",
//                })
@Controller
@EnableConfigurationProperties(StorageProperties.class)
public class Application extends SpringBootServletInitializer {


    public Application() {


    }


    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}