package com.zhangfuwen;

import com.zhangfuwen.collector.Config;
import com.zhangfuwen.collector.Gateway;
import com.zhangfuwen.collector.Persistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@RestController
public class Application extends SpringBootServletInitializer {
    public Application() {


    }

    @RequestMapping("/home1")
    public String index(Model model) {
        model.addAttribute("msg", "hello");
        return "index";
    }



    public static void main(String[] args) {
        Config config = Config.getInstance();
        Gateway gateway = config.getGateway();
        if(!config.isDevMode()) {
            try {
                gateway.init();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("scheduled to collect at " + new Date());
                try {
                    gateway.collectAndPersist(config.isDevMode(), Persistence.getEntityManager());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },0, config.getInterval()*1000);

        SpringApplication.run(Application.class, args);
    }
}