package fr.demo.state.monitor;

import fr.demo.state.common.What;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.state.EnumState;
import org.springframework.statemachine.transition.Transition;

import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class DemoStateMachineMonitorTest {

    @Mock
    private StateMachine<OrderState, OrderEvent> stateMachine;

    @Mock
    private Transition<OrderState, OrderEvent> transition;

    @Mock
    private Action<OrderState, OrderEvent> action;

    @Test
    public void test_transition_no_source() {
        DemoStateMachineMonitor<OrderState, OrderEvent> demoStateMachineMonitor = new DemoStateMachineMonitor<>();

        // mock
        Mockito.doReturn(null).when(transition).getSource();

        // assert => nothing
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(0);

        // call
        demoStateMachineMonitor.transition(stateMachine, transition, 10);

        // assert => still nothing
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(0);
    }

    @Test
    public void test_transition_unknown_state_machine() {
        DemoStateMachineMonitor<OrderState, OrderEvent> demoStateMachineMonitor = new DemoStateMachineMonitor<>();

        // mock
        Mockito.doReturn(new EnumState<OrderState, OrderEvent>(OrderState.INITIAL)).when(transition).getSource();
        Mockito.doReturn("XXXX").when(stateMachine).getId();

        // call
        try {
            demoStateMachineMonitor.transition(stateMachine, transition, 10);
            Assertions.failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e).hasMessage("No enum constant fr.demo.state.common.What.XXXX");
        }

        // assert => still nothing
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getTotalDuration()).isEqualTo(0);
    }

    @Test
    public void test_transition() {
        DemoStateMachineMonitor<OrderState, OrderEvent> demoStateMachineMonitor = new DemoStateMachineMonitor<>();

        // mock
        Mockito.doReturn(new EnumState<OrderState, OrderEvent>(OrderState.INITIAL)).when(transition).getSource();
        Mockito.doReturn(new EnumState<OrderState, OrderEvent>(OrderState.DRAFT)).when(transition).getTarget();
        Mockito.doReturn(What.ORDER.name()).when(stateMachine).getId();

        // assert => nothing
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(0);

        // call
        demoStateMachineMonitor.transition(stateMachine, transition, 20);

        // assert => add duration
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getSince().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(1);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getTotalDuration()).isEqualTo(20);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getAverageDuration()).isEqualTo(20);

        // call
        demoStateMachineMonitor.transition(stateMachine, transition, 15);

        // assert => add duration
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getSince().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(2);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getTotalDuration()).isEqualTo(35);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getAverageDuration()).isEqualTo(17);

        // call
        demoStateMachineMonitor.transition(stateMachine, transition, 10);

        // assert => add duration
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getSince().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(3);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getTotalDuration()).isEqualTo(45);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getAverageDuration()).isEqualTo(15);

        // call
        DemoStateMachineMonitor.resetMonitor(What.ORDER);

        // assert => reset duration
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getSince().toLocalDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(0);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getTotalDuration()).isEqualTo(0);
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getAverageDuration()).isEqualTo(0);
    }

    @Test
    public void test_action() {
        DemoStateMachineMonitor<OrderState, OrderEvent> demoStateMachineMonitor = new DemoStateMachineMonitor<>();

        // assert => nothing
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(0);

        // call
        demoStateMachineMonitor.action(stateMachine, action, 10);

        // assert => still nothing
        Assertions.assertThat(DemoStateMachineMonitor.getMonitor(What.ORDER).getCount()).isEqualTo(0);
    }

}