package fr.demo.state.order;

import fr.demo.state.common.DemoEvent;

public enum OrderEvent implements DemoEvent {

    INIT,
    DELETE,

    VALIDATE_PREPARATION,
    UNVALIDATE_PREPARATION,

    RECEIVE,
    CANCEL_RECEIVE,

    START_PREPARATION,
    CANCEL_ALL_PASSES,
    START_DISPATCH,

    VALIDATE_RECEIPT,

    VALIDATE_DISPATCH,
    UNVALIDATE_DISPATCH,

    FIXME, VALIDATE_DELIVERY
}
