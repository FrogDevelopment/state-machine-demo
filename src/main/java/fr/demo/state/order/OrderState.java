package fr.demo.state.order;

import fr.demo.state.common.DemoState;

public enum OrderState implements DemoState {
    INITIAL,

    DRAFT,
    WAITING_PAYMENT,
    PREPARING,
    DELIVERING,
    DONE,
    CANCELED
}
