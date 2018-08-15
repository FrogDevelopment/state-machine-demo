package fr.demo.state.order.config;

import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.security.SecuredStateMachineConfigureAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@EnableStateMachineFactory(name = "orderMachine")
public class OrderMachineConfig extends SecuredStateMachineConfigureAdapter<OrderState, OrderEvent> {

    @Autowired
    private OrderFlowGuard fromStockFlowGuard;

    @Autowired
    private OrderFlowGuard jitFlowGuard;

    @Autowired
    private OrderFlowGuard toStockFlowGuard;

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
              .states(EnumSet.allOf(OrderState.class))
              .initial(OrderState.INITIAL)
              .choice(OrderState.CHOICE_INIT)
              .choice(OrderState.CHOICE_CANCEL)
              .end(OrderState.DELIVERY_COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                // INITIAL
                .withExternal().
                        source(OrderState.INITIAL).
                        target(OrderState.CHOICE_INIT).
                        event(OrderEvent.INIT)
                // le CHOICE permet la sélection de l'état selon le type de commande
                .and()
                .withChoice().
                        source(OrderState.CHOICE_INIT).
                        first(OrderState.TO_PREPARE, fromStockFlowGuard).
                        last(OrderState.TO_RECEIVE)

                // RECEIPT
                .and()
                .withExternal().
                        source(OrderState.TO_RECEIVE).
                        target(OrderState.INITIAL).
                        event(OrderEvent.DELETE)
                .and()
                .withExternal().
                        source(OrderState.TO_RECEIVE).
                        target(OrderState.RECEIVED).
                        event(OrderEvent.RECEIVE)
                .and()


                .withExternal().
                        source(OrderState.RECEIVED).
                        target(OrderState.TO_RECEIVE).
                        event(OrderEvent.CANCEL_RECEIVE)
                .and()
                .withExternal().
                        source(OrderState.RECEIVED).
                        target(OrderState.DISPATCH_IN_PROGRESS).
                        event(OrderEvent.START_DISPATCH).guard(jitFlowGuard)

//                .and()
//                .withExternal().
//                        source(OrderState.DISPATCH_IN_PROGRESS).
//                        target(OrderState.RECEIVED).
//                        event(OrderEvent.CANCEL_ALL_PASSES)
                .and()
                .withExternal().
                        source(OrderState.DISPATCH_IN_PROGRESS).
                        target(OrderState.CHOICE_CANCEL).
                        event(OrderEvent.CANCEL_ALL_PASSES)
                // le CHOICE permet la sélection de l'état selon le type de commande
                .and()
                .withChoice().
                        source(OrderState.CHOICE_CANCEL).
                        first(OrderState.RECEIVED, jitFlowGuard). // jit => back to RECEIVED
                        last(OrderState.DISPATCH_IN_PROGRESS) // from_stock order => stay DISPATCH_IN_PROGRESS

                // PREPARATION
                .and()
                .withExternal().
                        source(OrderState.TO_PREPARE).
                        target(OrderState.INITIAL).
                        event(OrderEvent.DELETE)
                .and()
                .withExternal().
                        source(OrderState.TO_PREPARE).
                        target(OrderState.AWAITING_PREPARATION).
                        event(OrderEvent.VALIDATE_PREPARATION)
                .and()
                .withExternal().
                        source(OrderState.AWAITING_PREPARATION).
                        target(OrderState.TO_PREPARE).
                        event(OrderEvent.UNVALIDATE_PREPARATION)
                .and()
                .withExternal().
                        source(OrderState.AWAITING_PREPARATION).
                        target(OrderState.DISPATCH_IN_PROGRESS).
                        event(OrderEvent.START_PREPARATION)

                // STOCK
                .and()
                .withExternal().
                        source(OrderState.RECEIVED).
                        target(OrderState.TO_STOCK).
                        event(OrderEvent.VALIDATE_RECEIPT).guard(toStockFlowGuard)
                .and()
                .withExternal().
                        source(OrderState.TO_STOCK).
                        target(OrderState.DELIVERY_COMPLETED).
                        event(OrderEvent.FIXME)

                // DISPATCH
                .and()
                .withExternal().
                        source(OrderState.DISPATCH_IN_PROGRESS).
                        target(OrderState.DISPATCH_VALIDATED).
                        event(OrderEvent.VALIDATE_DISPATCH)
                .and()
                .withExternal().
                        source(OrderState.DISPATCH_VALIDATED).
                        target(OrderState.DISPATCH_IN_PROGRESS).
                        event(OrderEvent.UNVALIDATE_DISPATCH)

                // DELIVERY
                .and()
                .withExternal().
                        source(OrderState.DISPATCH_VALIDATED).
                        target(OrderState.DELIVERY_COMPLETED).
                        event(OrderEvent.VALIDATE_DELIVERY)
        ;
    }
}
