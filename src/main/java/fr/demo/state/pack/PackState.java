package fr.demo.state.pack;

import fr.demo.state.common.DemoState;

public enum PackState implements DemoState {
    INITIAL,

    TO_PACKAGE,
    PACKAGING,
    DELIVERING,
    RECEIVED
}
