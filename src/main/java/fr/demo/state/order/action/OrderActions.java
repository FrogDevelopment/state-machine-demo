package fr.demo.state.order.action;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.OnStateExit;
import org.springframework.statemachine.annotation.OnStateMachineError;
import org.springframework.statemachine.annotation.OnTransitionEnd;
import org.springframework.statemachine.annotation.OnTransitionStart;
import org.springframework.statemachine.annotation.WithStateMachine;

// here exception too doesn't interrupt the transition
@WithStateMachine(id = "ORDER")
public class OrderActions {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderActions.class);

    private void logMessage(String message, StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("{} for Order '{}' from [{}] to [{}]",
                message,
                context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class),
                context.getSource() != null ? context.getSource().getId() : "unknown",
                context.getTarget() != null ? context.getTarget().getId() : "unknown");
    }

    @OnStateMachineError
    public void onStateMachineError(Exception e) {
        LOGGER.error("State Machine for Order on error", e);
    }

    @OrderOnTransition(source = OrderState.PREPARING, target = OrderState.DELIVERING)
    public void logOnTransition(StateContext<OrderState, OrderEvent> context) {
        logMessage("Transition", context);
    }

    @OnTransitionStart()
    public void logOnTransitionStart(StateContext<OrderState, OrderEvent> context) {
        logMessage("Transition start", context);
    }

    @OnTransitionEnd()
    public void logOnTransitionEnd(StateContext<OrderState, OrderEvent> context) {
        logMessage("Transition end", context);
    }

    @OrderOnStateChanged(source = OrderState.INITIAL, target = OrderState.DRAFT)
    public void logOnStateChanged(StateContext<OrderState, OrderEvent> context) {
        logMessage("State changed", context);
    }

    @OnStateExit()
    public void logOnStateExit(StateContext<OrderState, OrderEvent> context) {
        logMessage("State exit", context);
    }

    @OnStateEntry()
    public void logOnStateEntry(StateContext<OrderState, OrderEvent> context) {
        logMessage("State entry", context);
    }

    @OrderOnEventNotAccepted()
    public void logOnEventNotAccepted(StateContext<OrderState, OrderEvent> context) {
        logMessage("State change refused", context);
    }
}