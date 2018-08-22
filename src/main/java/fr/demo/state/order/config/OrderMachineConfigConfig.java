package fr.demo.state.order.config;

import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import org.springframework.statemachine.config.AbstractStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@EnableStateMachineFactory(name = "orderMachine")
public class OrderMachineConfigConfig extends AbstractStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
                .states(EnumSet.allOf(OrderState.class))
                .initial(OrderState.INITIAL)
                .end(OrderState.RECEIVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                // INITIAL
                .withExternal().
                source(OrderState.INITIAL).
                target(OrderState.DRAFT).
                event(OrderEvent.CREATE)
                .and()
                .withExternal().
                source(OrderState.DRAFT).
                target(OrderState.CANCELED).
                event(OrderEvent.CANCEL)
                .and()
                .withExternal().
                source(OrderState.DRAFT).
                target(OrderState.PREPARING).
                event(OrderEvent.VALIDATE)
                .and()
                .withExternal().
                source(OrderState.PREPARING).
                target(OrderState.DELIVERING).
                event(OrderEvent.SEND)
                .and()
                .withExternal().
                source(OrderState.DELIVERING).
                target(OrderState.RECEIVED).
                event(OrderEvent.RECEIPT)
                .and()
                .withExternal().
                source(OrderState.DELIVERING).
                target(OrderState.RECEIVED).
                event(OrderEvent.RECEIPT)
        ;
    }
}
