package fr.demo.state.order.config;

import fr.demo.state.order.Flow;
import fr.demo.state.order.data.OrderDao;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.guard.Guard;

import fr.demo.state.common.AbstractStatePersist;

public class OrderFlowGuard implements Guard<OrderState, OrderEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFlowGuard.class);

    private final Flow targetFlow;

    @Autowired
    private OrderDao orderDao;

    public OrderFlowGuard(Flow targetFlow) {
        this.targetFlow = targetFlow;
    }

    @Override
    public boolean evaluate(StateContext<OrderState, OrderEvent> context) {
        LOGGER.info("Guard ORDER against event {}", context.getEvent());

        MessageHeaders headers = context.getMessage().getHeaders();
        if (headers.containsKey(AbstractStatePersist.HN_CODE)) {
            String orderCode = headers.get(AbstractStatePersist.HN_CODE, String.class);

            Flow orderFlow = orderDao.getOrderFlow(orderCode);

            LOGGER.debug("ORDER '{}' evaluate flow={}", orderCode, orderFlow);

            return targetFlow.equals(orderFlow);
        } else {
            LOGGER.error("No request found, ORDER can not be evaluate");
            throw new StateMachineException("Unable to get request on header to evaluate ORDER");
        }
    }
}
