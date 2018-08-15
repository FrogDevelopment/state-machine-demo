package fr.demo.state.preparation.config;

import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import fr.demo.state.security.SecuredStateMachineConfigureAdapter;

import java.util.EnumSet;

@EnableStateMachineFactory(name = "preparationMachine")
public class PreparationMachineConfig extends SecuredStateMachineConfigureAdapter<PreparationState, PreparationEvent> {

    // fixme actions à ajouter dans tests

    @Autowired
    private PreparationAction preparationActionInit;

    @Autowired
    private PreparationAction preparationActionDelete;

    @Autowired
    private PreparationAction preparationActionStart;

    @Override
    public void configure(StateMachineStateConfigurer<PreparationState, PreparationEvent> states) throws Exception {
        states.withStates()
              .initial(PreparationState.INITIAL)
              .states(EnumSet.allOf(PreparationState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PreparationState, PreparationEvent> transitions) throws Exception {
        transitions
                //
                .withExternal().
                        source(PreparationState.INITIAL).
                        target(PreparationState.CREATED).
                        event(PreparationEvent.INIT).
                        action(preparationActionInit)

                .and()
                .withExternal().
                        source(PreparationState.CREATED).
                        target(PreparationState.INITIAL).
                        event(PreparationEvent.DELETE).
                        action(preparationActionDelete)
                //
                .and()
                .withExternal().
                        source(PreparationState.CREATED).
                        target(PreparationState.TO_PREPARE).
                        event(PreparationEvent.VALIDATE)
                //
                .and()
                .withExternal().
                        source(PreparationState.TO_PREPARE).
                        target(PreparationState.CREATED).
                        event(PreparationEvent.UNVALIDATE)
                //
                .and()
                .withExternal().
                        source(PreparationState.TO_PREPARE).
                        target(PreparationState.IN_PROGRESS).
                        event(PreparationEvent.START).
                        action(preparationActionStart)

                //
                .and()
                .withExternal().
                        source(PreparationState.IN_PROGRESS).
                        target(PreparationState.DONE).
                        event(PreparationEvent.COMPLETE)
                //
                .and()
                .withExternal().
                        source(PreparationState.DONE).
                        target(PreparationState.CLOSED).
                        event(PreparationEvent.CLOSE)
        ;
    }
}
