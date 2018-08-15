package fr.demo.state.preparation.config;

import fr.demo.state.common.What;
import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:preparation-machine-context-test.xml"})
@WithMockUser
public class PreparationMachineConfigTest {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private StateMachineFactory<PreparationState, PreparationEvent> preparationMachineFactory;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private PreparationAction preparationActionInit;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private PreparationAction preparationActionDelete;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private PreparationAction preparationActionStart;

    @Test
    public void testPreparationMachine() throws Exception {
        StateMachineTestPlan<PreparationState, PreparationEvent> plan =
                StateMachineTestPlanBuilder.<PreparationState, PreparationEvent>builder()
                        .stateMachine(preparationMachineFactory.getStateMachine(What.PREPARATION.name()))
                        .step().expectState(PreparationState.INITIAL)
                        .and()
                        .step().sendEvent(PreparationEvent.INIT).expectStateChanged(1).expectState(PreparationState.CREATED)
                        .and()
                        .step().sendEvent(PreparationEvent.VALIDATE).expectStateChanged(1).expectState(PreparationState.TO_PREPARE)
                        .and()
                        .step().sendEvent(PreparationEvent.UNVALIDATE).expectStateChanged(1).expectState(PreparationState.CREATED)
                        .and()
                        .step().sendEvent(PreparationEvent.VALIDATE).expectStateChanged(1).expectState(PreparationState.TO_PREPARE)
                        .and()
                        .step().sendEvent(PreparationEvent.START).expectStateChanged(1).expectState(PreparationState.IN_PROGRESS)
                        .and()
                        .step().sendEvent(PreparationEvent.COMPLETE).expectStateChanged(1).expectState(PreparationState.DONE)
                        .and()
                        .step().sendEvent(PreparationEvent.CLOSE).expectStateChanged(1).expectState(PreparationState.CLOSED)
                        .and()
                        .build();
        plan.test();
    }

}