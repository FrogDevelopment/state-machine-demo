package fr.demo.state.order.guard;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class PaymentGuard implements Guard<OrderState, OrderEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGuard.class);

    @Override
    public boolean evaluate(StateContext<OrderState, OrderEvent> context) {
        String orderCode = context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class);

        boolean result = !"ORDER_3".equals(orderCode);

        LOGGER.info("GUARD : has order '{}' be paid => {}", orderCode, result);

        return result;
    }
}