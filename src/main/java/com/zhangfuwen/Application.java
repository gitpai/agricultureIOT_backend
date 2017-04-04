package com.zhangfuwen;

import com.zhangfuwen.collector.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@Controller
public class Application extends SpringBootServletInitializer {
    public static boolean running = false;
    public static Config config;
//    @Autowired
//    GatewayRepository gatewayRepository;
//    @Autowired
//    ZigbeeNodeRepository zigbeeNodeRepository;
//    @Autowired
//    CoilOrSensorRepository coilOrSensorRepository;

    public Application() {


    }

//    @RequestMapping("/start")
//    public String index(Model model, final RedirectAttributes redirectAttributes) {
//        if (!running) {
//            running = true;
//            startCollecting();
//
//
//        }
//
//        redirectAttributes.addFlashAttribute("message", "启动成功");
//        redirectAttributes.addFlashAttribute("referer", "/index");
//        return "flash";
//    }

    private static void startCollecting() {
        // Iterable<Gateway> gateways = gatewayRepository.findAll();
        List<Gateway> gateways = Persistence.getEntityManager().createQuery("select g from Gateway g").getResultList();
        //Persistence.getEntityManager().close();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
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
            }
        },0, config.getInterval() * 1000);
    }


    public static void main(String[] args) {
        config = Config.getInstance();
        startCollecting();
        SpringApplication.run(Application.class, args);
    }
}