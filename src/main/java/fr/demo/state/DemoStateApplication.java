package fr.demo.state;

import fr.demo.state.monitor.DemoStateMachineMonitor;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.config.OrderFlowGuard;
import fr.demo.state.preparation.config.PreparationAction;
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

    // DEFINITION ACTION FOR ORDER
    @Bean
    public OrderFlowGuard fromStockFlowGuard() {
        return new OrderFlowGuard("FROM_STOCK");
    }
    @Bean
    public OrderFlowGuard jitFlowGuard() {
        return new OrderFlowGuard("JIT");
    }
    @Bean
    public OrderFlowGuard toStockFlowGuard() {
        return new OrderFlowGuard("TO_STOCK");
    }

    // DEFINITION ACTION FOR PREPARATION
    @Bean
    public PreparationAction preparationActionInit() {
        return new PreparationAction(OrderEvent.VALIDATE_PREPARATION);
    }

    @Bean
    public PreparationAction preparationActionDelete() {
        return new PreparationAction(OrderEvent.UNVALIDATE_PREPARATION);
    }

    @Bean
    public PreparationAction preparationActionStart() {
        return new PreparationAction(OrderEvent.START_PREPARATION);
    }
}
