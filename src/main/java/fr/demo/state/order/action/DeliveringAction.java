package fr.demo.state.order.action;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.common.MessageService;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class DeliveringAction implements Action<OrderState, OrderEvent> {

    @Autowired
    private MessageService messageService;

    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {
        messageService.sendMail(String.format("Commande '%s' en cours d'envoi", context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class)));
    }
}
