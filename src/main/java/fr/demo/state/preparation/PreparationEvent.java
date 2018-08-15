package fr.demo.state.preparation;

import fr.demo.state.common.DemoEvent;

public enum PreparationEvent implements DemoEvent {

    INIT,
    DELETE,

    VALIDATE,
    UNVALIDATE,
    START,
    COMPLETE,
    CLOSE

}
