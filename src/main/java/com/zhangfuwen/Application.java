package com.zhangfuwen;

import com.zhangfuwen.collector.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
@Controller
public class Application extends SpringBootServletInitializer {
    public static Config config;
    //该值置空时即重新从数据库读取列表
    public static List<Gateway> gateways=null;
    public static Lock gatewayLock;

    public Application() {


    }

    private static void startCollecting() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                gatewayLock.lock();
                if(gateways==null) {
                    gateways = Persistence.getEntityManager().createQuery("select g from Gateway g").getResultList();
                }
                for (Gateway gateway : gateways) {
                    if (!config.isDevMode()) {
                        try {
                            gateway.init();
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }

                    System.out.println("scheduled to collect at " + new Date());
                    try {
                        gateway.collectAndPersist(config.isDevMode(), Persistence.getEntityManager());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                gatewayLock.unlock();
            }
        },0, config.getInterval() * 1000);
    }


    public static void main(String[] args) {
        gatewayLock = new ReentrantLock();
        config = Config.getInstance();
        startCollecting();
        SpringApplication.run(Application.class, args);
    }
}