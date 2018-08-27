package fr.demo.state.pack.config;

import fr.demo.state.pack.PackEvent;
import fr.demo.state.pack.PackState;
import fr.demo.state.pack.action.PackAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.AbstractStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@EnableStateMachineFactory(name = "packMachine")
public class PackMachineConfig extends AbstractStateMachineConfigurerAdapter<PackState, PackEvent> {


    @Autowired
    private PackAction packagingAction;

    @Autowired
    private PackAction deliveringPackAction;

    @Autowired
    private PackAction receivedAction;

    @Override
    public void configure(StateMachineStateConfigurer<PackState, PackEvent> states) throws Exception {
        states.withStates()
                .states(EnumSet.allOf(PackState.class))
                .initial(PackState.INITIAL)
                .end(PackState.RECEIVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PackState, PackEvent> transitions) throws Exception {
        transitions
                // INITIAL
                .withExternal()
                .source(PackState.INITIAL)
                .target(PackState.TO_PACKAGE)
                .event(PackEvent.CREATE)

                .and()
                .withExternal()
                .source(PackState.TO_PACKAGE)
                .target(PackState.PACKAGING)
                .event(PackEvent.PROCESS)
                .action(packagingAction)

                .and()
                .withExternal()
                .source(PackState.PACKAGING)
                .target(PackState.DELIVERING)
                .event(PackEvent.SEND)
                .action(deliveringPackAction)

                .and()
                .withExternal()
                .source(PackState.DELIVERING)
                .target(PackState.RECEIVED)
                .event(PackEvent.RECEIPT)
                .action(receivedAction)
        ;
    }
}
