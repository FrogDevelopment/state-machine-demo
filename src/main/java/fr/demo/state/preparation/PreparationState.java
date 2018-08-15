package fr.demo.state.preparation;

import fr.demo.state.common.DemoState;

public enum PreparationState implements DemoState {

    INITIAL,

    CREATED,
    TO_PREPARE,
    IN_PROGRESS,
    DONE,
    CLOSED


}
