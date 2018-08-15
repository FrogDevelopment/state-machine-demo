package fr.demo.state.preparation.config;

import fr.demo.state.preparation.data.PreparationDao;
import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.action.Action;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.data.OrderStatePersist;

import java.util.List;

class PreparationAction implements Action<PreparationState, PreparationEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreparationAction.class);

    private final OrderEvent orderEvent;

    @Autowired
    private PreparationDao preparationDao;

    @Autowired
    private OrderStatePersist orderStatePersist;

    PreparationAction(OrderEvent orderEvent) {
        this.orderEvent = orderEvent;
    }

    @Override
    public void execute(StateContext<PreparationState, PreparationEvent> context) {
        LOGGER.info("PREPARATION state change => notify ORDERs with event {}", orderEvent);

        MessageHeaders headers = context.getMessage().getHeaders();
        if (headers.containsKey(AbstractStatePersist.HN_CODE)) {
            String code = headers.get(AbstractStatePersist.HN_CODE, String.class);

            notifyOrderStateMachine(code, context.getEvent());
        } else {
            LOGGER.error("No request found, ORDERs not notified");
            throw new StateMachineException("Unable to get request on header to notify ORDERs with event " + orderEvent);
        }
    }

    /**
     * change all ORDERs associate with the PREPARATION
     */
    private void notifyOrderStateMachine(String code, PreparationEvent event) {
        List<String> orderCodes = preparationDao.getOrderCodesOfPreparation(code);

        LOGGER.debug("Event '{}' sent for PREPARATION '{}' => send OrderEvent '{}' to associated orders {}", event, code, orderEvent, String.join(", ", orderCodes));

        for (String orderCode : orderCodes) {
            orderStatePersist.change(orderCode, orderEvent);
        }
    }
}
