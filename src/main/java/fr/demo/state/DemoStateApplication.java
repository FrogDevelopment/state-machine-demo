package fr.demo.state;

import fr.demo.state.monitor.DemoStateMachineMonitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties
@SpringBootApplication(scanBasePackages = "fr.demo.state")
public class DemoStateApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoStateApplication.class, args);
    }

    @Bean
    public DemoStateMachineMonitor stateMachineMonitor() {
        return new DemoStateMachineMonitor();
    }

}
