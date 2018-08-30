package fr.demo.state.pack.action;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.data.OrderStatePersist;
import fr.demo.state.pack.PackEvent;
import fr.demo.state.pack.PackState;
import fr.demo.state.pack.data.PackDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class PackAction implements Action<PackState, PackEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackAction.class);

    @Autowired
    private PackDao packDao;

    @Lazy
    @Autowired
    private OrderStatePersist orderStatePersist;

    private final OrderEvent orderEvent;

    public PackAction(OrderEvent orderEvent) {
        this.orderEvent = orderEvent;
    }

    @Override
    public void execute(StateContext<PackState, PackEvent> context) {
        String packCode = context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class);

        String orderCode = packDao.getOrderCode(packCode);
        LOGGER.info("Notifying order '{}' event {{}} for pack '{}'", orderCode, orderEvent, packCode);
        orderStatePersist.change(orderCode, orderEvent);
    }
}
