package fr.demo.state.order.action;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.pack.PackEvent;
import fr.demo.state.pack.PackState;
import fr.demo.state.pack.data.PackDao;
import fr.demo.state.pack.data.PackStatePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ToPrepareAction implements Action<OrderState, OrderEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToPrepareAction.class);

    @Autowired
    private PackDao packDao;

    @Autowired
    private PackStatePersist packStatePersist;

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        String orderCode = context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class);

        // pack creation on db
        String packCode = "PACK_" + orderCode;
        packDao.create(Map.of("code", packCode, "state", PackState.INITIAL.name(), "orderCode", orderCode));

        LOGGER.info("Notifying pack creation for order '{}'", orderCode);

        packStatePersist.change(packCode, PackEvent.CREATE);
    }
}
