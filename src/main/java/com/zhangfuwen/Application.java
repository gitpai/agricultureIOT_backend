package com.zhangfuwen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
//        Config config = Config.getInstance();
//        Gateway com.zhangfuwen.gateway = config.getGateway();
//        if(!config.isDevMode()) {
//            try {
//                com.zhangfuwen.gateway.init();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//        }
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("scheduled to collect at " + new Date());
//                try {
//                    com.zhangfuwen.gateway.collectAndPersist(config.isDevMode(), Persistence.getEntityManager());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        },0, config.getInterval()*1000);

        SpringApplication.run(Application.class, args);
    }
}