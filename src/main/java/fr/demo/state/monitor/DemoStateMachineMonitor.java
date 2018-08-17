package fr.demo.state.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.monitor.StateMachineMonitor;
import org.springframework.statemachine.transition.Transition;

public class DemoStateMachineMonitor<S extends Enum<S>, E extends Enum<E>> implements StateMachineMonitor<S, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoStateMachineMonitor.class);

    @Override
    public void transition(StateMachine<S, E> stateMachine, Transition<S, E> transition, long duration) {
        if (transition.getSource() == null) {
            return;
        }

        LOGGER.debug("Transition monitor of [{}] : from={}, to={}, duration={}ms", stateMachine.getId(), transition.getSource().getId(), transition.getTarget().getId(), duration);
    }

    @Override
    public void action(StateMachine<S, E> stateMachine, Action<S, E> action, long duration) {
        // todo ?
    }

}
