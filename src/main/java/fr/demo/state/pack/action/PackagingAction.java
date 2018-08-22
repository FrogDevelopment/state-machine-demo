package fr.demo.state.pack.action;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.pack.PackEvent;
import fr.demo.state.pack.PackState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class PackagingAction implements Action<PackState, PackEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackagingAction.class);

    @Override
    public void execute(StateContext<PackState, PackEvent> context) {
        String packCode = context.getMessageHeaders().get(AbstractStatePersist.HN_CODE, String.class);

        LOGGER.info("Notifying order preparation for pack '{}'", packCode);
    }
}
