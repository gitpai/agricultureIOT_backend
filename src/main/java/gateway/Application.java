package gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        try {
            Gateway gateway = new Gateway("./config.xml");
            gateway.run();
        }catch (IOException e) {
            System.out.println("gateway error:"+e.getMessage());
            e.printStackTrace();
        }
        SpringApplication.run(Application.class, args);
    }
}