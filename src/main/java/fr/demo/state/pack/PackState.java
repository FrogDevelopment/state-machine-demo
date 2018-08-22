package fr.demo.state.pack;

import fr.demo.state.common.DemoState;

public enum PackState implements DemoState {
    INITIAL,

    PACKAGING,
    DELIVERING,
    RECEIVED
}
