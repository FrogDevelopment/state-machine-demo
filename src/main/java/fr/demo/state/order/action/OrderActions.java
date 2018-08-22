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

    private String getCode(StateContext<OrderState, OrderEvent> context) {
        return context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class);
    }

    @OnStateMachineError
    public void onStateMachineError(Exception e) {
        LOGGER.error("State Machine for Order on error", e);
    }

    @OrderOnTransition(source = OrderState.PREPARING, target = OrderState.DELIVERING)
    public void logOnTransition(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("Transition for Order '{}' from [{}] to [{}]", getCode(context), context.getSource().getId(), context.getTarget().getId());
    }

    @OnTransitionStart()
    public void logOnTransitionStart(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("Transition start for Order '{}' from [{}] to [{}]", getCode(context), context.getSource().getId(), context.getTarget().getId());
    }

    @OnTransitionEnd()
    public void logOnTransitionEnd(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("Transition end for Order '{}' from [{}] to [{}]", getCode(context), context.getSource().getId(), context.getTarget().getId());
    }

    @OrderOnStateChanged(source = OrderState.INITIAL, target = OrderState.DRAFT)
    public void logOnStateChanged(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("State changed for Order '{}' from [{}] to [{}]", getCode(context), context.getSource().getId(), context.getTarget().getId());
    }

    @OnStateExit()
    public void logOnStateExit(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("State exit for Order '{}' from [{}]", getCode(context), context.getSource().getId());
    }

    @OnStateEntry()
    public void logOnStateEntry(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("State entry for Order '{}' to [{}]", getCode(context), context.getTarget().getId());
    }

    @OrderOnEventNotAccepted()
    public void logOnEventNotAccepted(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("State change refused for Order '{}' with event = ", context.getMessageHeaders().get(AbstractStatePersist.HN_CODE), context.getEvent());
    }
}