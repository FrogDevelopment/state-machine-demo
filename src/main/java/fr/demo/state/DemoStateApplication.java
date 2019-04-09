package fr.demo.state;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = "fr.demo.state")
public class DemoStateApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoStateApplication.class).run(args);
    }
}
