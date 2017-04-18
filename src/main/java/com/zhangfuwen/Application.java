package com.zhangfuwen;

import com.zhangfuwen.collector.*;
import com.zhangfuwen.info.ThresholdInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
@EnableScheduling
@Controller
public class Application extends SpringBootServletInitializer {


    public Application() {


    }


    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}