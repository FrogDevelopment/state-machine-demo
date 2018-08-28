package fr.demo.state.order.config;

import fr.demo.state.common.MessageService;
import fr.demo.state.common.What;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.guard.ChoiceGuard;
import fr.demo.state.order.guard.PaymentGuard;
import fr.demo.state.pack.PackEvent;
import fr.demo.state.pack.data.PackDao;
import fr.demo.state.pack.data.PackStatePersist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:order-machine-context-test.xml")
public class OrderMachineConfigTest {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> orderMachineFactory;

    @SpyBean
    private MessageService messageService;

    @MockBean
    private PaymentGuard paymentGuard;

    @MockBean
    private ChoiceGuard choiceGuard;

    @MockBean
    private PackDao packDao;
    @MockBean
    private PackStatePersist packStatePersist;

    @Test
    public void test_guard_false() throws Exception {
        // given
        doReturn(false).when(paymentGuard).evaluate(any());

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step()
                        .expectState(OrderState.INITIAL)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.CREATE)
                        .expectState(OrderState.DRAFT)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.VALIDATE)
                        .expectState(OrderState.WAITING_PAYMENT)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.PAY)
                        .expectState(OrderState.WAITING_PAYMENT)

                        .and()
                        .build();
        plan.test();

        verifyZeroInteractions(packDao);
        verifyZeroInteractions(packStatePersist);
        verifyZeroInteractions(messageService);
    }

    @Test
    public void test_guard_true_and_choice_false() throws Exception {
        // given
        doReturn(true).when(paymentGuard).evaluate(any());
        doReturn(false).when(choiceGuard).evaluate(any());

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step()
                        .expectState(OrderState.INITIAL)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.CREATE)
                        .expectState(OrderState.DRAFT)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.VALIDATE)
                        .expectState(OrderState.WAITING_PAYMENT)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.PAY)
                        .expectState(OrderState.TO_PREPARE)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.PREPARE)
                        .expectState(OrderState.PREPARING)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.SEND)
                        .expectState(OrderState.DELIVERING)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.RECEIPT)
                        .expectState(OrderState.DONE)

                        .and()
                        .build();
        plan.test();

        verify(packDao).create(null); // fixme the code on the context
        verify(packStatePersist).change(null,  PackEvent.CREATE);
        verify(messageService, times(2)).sendMail(anyString());
    }

    @Test
    public void test_guard_true_and_choice_true() throws Exception {
        // given
        doReturn(true).when(paymentGuard).evaluate(any());
        doReturn(true).when(choiceGuard).evaluate(any());

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step()
                        .expectState(OrderState.INITIAL)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.CREATE)
                        .expectState(OrderState.DRAFT)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.VALIDATE)
                        .expectState(OrderState.WAITING_PAYMENT)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.PAY)
                        .expectState(OrderState.DONE)

                        .and()
                        .build();
        plan.test();

        verifyZeroInteractions(packDao);
        verifyZeroInteractions(packStatePersist);
        verifyZeroInteractions(messageService);
    }

    @Test
    public void cancel() throws Exception {
        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step()
                        .expectState(OrderState.INITIAL)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.CREATE)
                        .expectState(OrderState.DRAFT)

                        .and()
                        .step()
                        .sendEvent(OrderEvent.CANCEL)
                        .expectState(OrderState.CANCELED)

                        .and()
                        .build();
        plan.test();

        verifyZeroInteractions(packDao);
        verifyZeroInteractions(packStatePersist);
        verifyZeroInteractions(messageService);
    }

}