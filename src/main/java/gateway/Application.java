package gateway;

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
    public Application() {


    }

    public static void main(String[] args) {
        Config config = Config.getInstance();
        Gateway gateway = config.getGateway();
        try {
            gateway.init();
        }catch (IOException e) {
            e.printStackTrace();
            return;
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