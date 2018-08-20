package fr.demo.state.common;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachineContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DemoStateMachineContext<S extends DemoState, E extends DemoEvent> implements StateMachineContext<S, E> {

    private final What what;
    private final S state;

    /**
     * Instantiates a new default state machine context.
     *
     * @param state the state
     * @param id    the machine id
     */
    public DemoStateMachineContext(S state, What what) {
        this.state = state;
        this.what = what;
    }

    @Override
    public String getId() {
        return what.name();
    }

    @Override
    public List<StateMachineContext<S, E>> getChilds() {
        return Collections.emptyList();
    }

    @Override
    public S getState() {
        return state;
    }

    @Override
    public Map<S, S> getHistoryStates() {
        return Map.of();
    }

    @Override
    public E getEvent() {
        return null;
    }

    @Override
    public Map<String, Object> getEventHeaders() {
        return Map.of();
    }

    @Override
    public ExtendedState getExtendedState() {
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("what", what)
                .append("state", state)
                .toString();
    }
}
