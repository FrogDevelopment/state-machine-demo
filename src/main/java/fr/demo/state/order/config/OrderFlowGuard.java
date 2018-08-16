package fr.demo.state.order.config;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.data.OrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.guard.Guard;

public class OrderFlowGuard implements Guard<OrderState, OrderEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFlowGuard.class);

    private final String targetType;

    @Autowired
    private OrderDao orderDao;

    public OrderFlowGuard(String targetType) {
        this.targetType = targetType;
    }

    @Override
    public boolean evaluate(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("Guard ORDER against event {}", context.getEvent());

        MessageHeaders headers = context.getMessage().getHeaders();
        if (headers.containsKey(AbstractStatePersist.HN_CODE)) {
            String orderCode = headers.get(AbstractStatePersist.HN_CODE, String.class);

            String orderType = orderDao.getOrderType(orderCode);

            LOGGER.debug("ORDER '{}' evaluate type={}", orderCode, orderType);

            return targetType.equals(orderType);
        } else {
            LOGGER.error("No request found, ORDER can not be evaluate");
            throw new StateMachineException("Unable to get request on header to evaluate ORDER");
        }
    }
}
