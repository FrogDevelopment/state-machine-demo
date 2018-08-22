package fr.demo.state.order.guard;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.data.OrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class ChoiceGuard implements Guard<OrderState, OrderEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoiceGuard.class);

    @Autowired
    private OrderDao orderDao;

    @Override
    public boolean evaluate(StateContext<OrderState, OrderEvent> context) {
        String orderCode = context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class);

        boolean result = orderDao.hasOnlyNumericProduct(orderCode);

        LOGGER.info("CHOICE : is order '{}' with only numeric products => {}", orderCode, result);

        return result;
    }
}
