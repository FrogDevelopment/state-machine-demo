package fr.demo.state.order;

import fr.demo.state.common.DemoEvent;

public enum OrderEvent implements DemoEvent {

    CREATE,
    VALIDATE,
    PAY,
    SEND,
    RECEIPT,
    CANCEL
}
