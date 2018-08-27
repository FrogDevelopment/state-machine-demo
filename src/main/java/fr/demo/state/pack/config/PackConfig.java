package fr.demo.state.pack.config;

import fr.demo.state.order.OrderEvent;
import fr.demo.state.pack.action.PackAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PackConfig {

    @Bean
    public PackAction packagingAction() {
        return new PackAction(OrderEvent.PREPARE);
    }


    @Bean
    public PackAction deliveringAction() {
        return new PackAction(OrderEvent.SEND);
    }

    @Bean
    public PackAction receivedAction() {
        return new PackAction(OrderEvent.RECEIPT);
    }

}
