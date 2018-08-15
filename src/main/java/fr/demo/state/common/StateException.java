package fr.demo.state.common;

public class StateException extends RuntimeException {

    private final String code;
    private final DemoEvent event;
    private final DemoState currentState;

    StateException(What what, String code, DemoEvent event, DemoState currentState) {
        super(String.format("Change of state refused: what=%s, code=%s, event=%s, current state=%s", what, code, event, currentState));
        this.currentState = currentState;
        this.code = code;
        this.event = event;
    }

    public String getCode() {
        return code;
    }

    public DemoEvent getEvent() {
        return event;
    }

    public DemoState getCurrentState() {
        return currentState;
    }
}
