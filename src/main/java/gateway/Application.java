package gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class Application {

    private  static Logger logger = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        Config config = Config.getInstance();
        Gateway gateway = config.getGateway();
        try {
            gateway.init();
        }catch (IOException e) {
            logger.error("failed to initialize connection to gateway, "+e.getMessage());
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("data collection triggered");
                try {
                    gateway.collectAndPersist(config.isDevMode(), Persistence.getEntityManager());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.info("data collection finished");
            }
        },0, config.getInterval()*1000);

        SpringApplication.run(Application.class, args);
    }
}