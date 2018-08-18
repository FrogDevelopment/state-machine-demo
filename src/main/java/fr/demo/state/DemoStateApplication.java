package fr.demo.state;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@SpringBootApplication(scanBasePackages = "fr.demo.state")
public class DemoStateApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoStateApplication.class, args);
    }
}
