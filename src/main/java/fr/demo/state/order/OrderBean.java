package fr.demo.state.order;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.config.OrderOnEventNotAccepted;
import fr.demo.state.order.config.OrderOnStateChanged;
import fr.demo.state.order.config.OrderOnTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.annotation.EventHeaders;
import org.springframework.statemachine.annotation.OnStateEntry;
import org.springframework.statemachine.annotation.OnStateExit;
import org.springframework.statemachine.annotation.OnStateMachineError;
import org.springframework.statemachine.annotation.OnStateMachineStart;
import org.springframework.statemachine.annotation.OnStateMachineStop;
import org.springframework.statemachine.annotation.OnTransitionEnd;
import org.springframework.statemachine.annotation.OnTransitionStart;
import org.springframework.statemachine.annotation.WithStateMachine;

import java.util.Map;

@WithStateMachine(id = "ORDER")
public class OrderBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBean.class);

    @OnStateMachineStart
    public void onStateMachineStart() {
        LOGGER.info("STATE MACHINE started");
    }

    @OnStateMachineStop
    public void onStateMachineStop() {
        LOGGER.info("STATE MACHINE stopped");
    }

    @OnStateMachineError
    public void onStateMachineError() {
        LOGGER.info("STATE MACHINE on error");
    }

    @OrderOnTransition(source = OrderState.INITIAL, target = OrderState.DRAFT)
    public void logOnTransition(@EventHeaders Map<String, Object> headers) {
        LOGGER.info("Transition for Order [{}]", headers.get(AbstractStatePersist.HN_CODE));
    }

    @OnTransitionStart(source = "INITIAL", target = "DRAFT")
    public void logOnTransitionStart(@EventHeaders Map<String, Object> headers) {
        LOGGER.info("Transition start for Order [{}]", headers.get(AbstractStatePersist.HN_CODE));
    }

    @OnTransitionEnd(source = "INITIAL", target = "DRAFT")
    public void logOnTransitionEnd(@EventHeaders Map<String, Object> headers) {
        LOGGER.info("Transition end for Order [{}]", headers.get(AbstractStatePersist.HN_CODE));
    }

    @OrderOnStateChanged(source = OrderState.INITIAL, target = OrderState.DRAFT)
    public void logOnStateChanged(@EventHeaders Map<String, Object> headers) {
        LOGGER.info("Order [{}] initialized", headers.get(AbstractStatePersist.HN_CODE));
    }

    @OnStateEntry(source = "INITIAL", target = "DRAFT")
    public void logOnStateEntry(@EventHeaders Map<String, Object> headers) {
        LOGGER.info("Order [{}] initialisation entry", headers.get(AbstractStatePersist.HN_CODE));
    }

    @OnStateExit(source = "INITIAL", target = "DRAFT")
    public void logOnStateExit(@EventHeaders Map<String, Object> headers) {
        LOGGER.info("Order [{}] initialisation exit", headers.get(AbstractStatePersist.HN_CODE));
    }

    @OrderOnEventNotAccepted(event = OrderEvent.CREATE)
    public void logOnEventNotAccepted(@EventHeaders Map<String, Object> headers) {
        LOGGER.info("Order [{}] state change refuse", headers.get(AbstractStatePersist.HN_CODE));
    }
}
