package fr.demo.state.pack;

import fr.demo.state.common.DemoEvent;

public enum PackEvent implements DemoEvent {

    CREATE,
    PROCESS,
    SEND,
    RECEIPT
}
