package fr.demo.state.order;

import fr.demo.state.common.DemoState;

public enum OrderState implements DemoState {
    INITIAL,
    CHOICE_INIT,
    CHOICE_CANCEL,
    TO_PREPARE,
    AWAITING_PREPARATION,
    TO_RECEIVE,
    RECEIVED,
    TO_STOCK,
    DISPATCH_IN_PROGRESS,
    DISPATCH_VALIDATED,
    DELIVERY_COMPLETED
}
