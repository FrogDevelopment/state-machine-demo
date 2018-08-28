package fr.demo.state.order.config;

import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.action.DeliveringAction;
import fr.demo.state.order.action.PreparingAction;
import fr.demo.state.order.guard.ChoiceGuard;
import fr.demo.state.order.guard.PaymentGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.AbstractStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@EnableStateMachineFactory(name = "orderMachine")
public class OrderMachineConfig extends AbstractStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Autowired
    private PreparingAction preparingAction;

    @Autowired
    private DeliveringAction deliveringAction;

    @Autowired
    private PaymentGuard paymentGuard;

    @Autowired
    private ChoiceGuard choiceGuard;

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
                .states(EnumSet.allOf(OrderState.class))
                .initial(OrderState.INITIAL)
                .choice(OrderState.CHOICE_PAYMENT)
                .end(OrderState.DONE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                // INITIAL
                .withExternal()
                .source(OrderState.INITIAL)
                .target(OrderState.DRAFT)
                .event(OrderEvent.CREATE)

                .and()
                .withExternal()
                .source(OrderState.DRAFT)
                .target(OrderState.CANCELED)
                .event(OrderEvent.CANCEL)

                .and()
                .withExternal()
                .source(OrderState.DRAFT)
                .target(OrderState.WAITING_PAYMENT)
                .event(OrderEvent.VALIDATE)

                .and()
                .withExternal()
                .source(OrderState.WAITING_PAYMENT)
                .target(OrderState.CHOICE_PAYMENT)
                .event(OrderEvent.PAY)
                .guard(paymentGuard)

                .and()
                .withChoice()
                .source(OrderState.CHOICE_PAYMENT)
                .first(OrderState.DONE, choiceGuard)
                .last(OrderState.TO_PREPARE)

                .and()
                .withExternal()
                .source(OrderState.TO_PREPARE)
                .target(OrderState.PREPARING)
                .event(OrderEvent.PREPARE)
                .action(preparingAction)

                .and()
                .withExternal()
                .source(OrderState.PREPARING)
                .target(OrderState.DELIVERING)
                .event(OrderEvent.SEND)
                .action(deliveringAction)

                .and()
                .withExternal()
                .source(OrderState.DELIVERING)
                .target(OrderState.DONE)
                .event(OrderEvent.RECEIPT)
        ;
    }
}
