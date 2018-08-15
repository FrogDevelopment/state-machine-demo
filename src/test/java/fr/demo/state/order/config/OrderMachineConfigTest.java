package fr.demo.state.order.config;

import fr.demo.state.common.What;

import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.config.OrderFlowGuard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.test.StateMachineTestPlan;
import org.springframework.statemachine.test.StateMachineTestPlanBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@WithMockUser
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:order-machine-context-test.xml")
public class OrderMachineConfigTest {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private StateMachineFactory<OrderState, OrderEvent> orderMachineFactory;

    @Autowired
    private OrderFlowGuard fromStockFlowGuard;

    @Autowired
    private OrderFlowGuard jitFlowGuard;

    @Autowired
    private OrderFlowGuard toStockFlowGuard;

    private void fromStock(boolean fromStock) {
        //noinspection unchecked
        Mockito.doReturn(fromStock).when(fromStockFlowGuard).evaluate(Mockito.any(StateContext.class));
    }

    private void toDispatch(boolean toDispatch) {
        //noinspection unchecked
        Mockito.doReturn(toDispatch).when(jitFlowGuard).evaluate(Mockito.any(StateContext.class));
    }

    private void toStock(boolean toStock) {
        //noinspection unchecked
        Mockito.doReturn(toStock).when(toStockFlowGuard).evaluate(Mockito.any(StateContext.class));
    }

    @Test
    public void test_receipt_to_prepare() throws Exception {
        fromStock(true);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_PREPARE)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_prepare_delete() throws Exception {
        fromStock(true);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_PREPARE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.DELETE).
                                expectState(OrderState.INITIAL)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_prepare_to_dispatch() throws Exception {
        fromStock(true);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_PREPARE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_PREPARATION).
                                expectState(OrderState.AWAITING_PREPARATION)
                        .and()
                        .step().
                                sendEvent(OrderEvent.START_PREPARATION).
                                expectState(OrderState.DISPATCH_IN_PROGRESS)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_DISPATCH).
                                expectState(OrderState.DISPATCH_VALIDATED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_DELIVERY).
                                expectState(OrderState.DELIVERY_COMPLETED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_prepare_cancel() throws Exception {
        fromStock(true);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_PREPARE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_PREPARATION).
                                expectState(OrderState.AWAITING_PREPARATION)
                        .and()
                        .step().
                                sendEvent(OrderEvent.UNVALIDATE_PREPARATION).
                                expectState(OrderState.TO_PREPARE)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_to_receipt() throws Exception {
        fromStock(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_received() throws Exception {
        fromStock(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.RECEIVE).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_to_receive_cancel() throws Exception {
        fromStock(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.RECEIVE).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.CANCEL_RECEIVE).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_delete() throws Exception {
        fromStock(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.DELETE).
                                expectState(OrderState.INITIAL)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_to_dispatch() throws Exception {
        fromStock(false);
        toDispatch(true);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.RECEIVE).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.START_DISPATCH).
                                expectState(OrderState.DISPATCH_IN_PROGRESS)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_DISPATCH).
                                expectState(OrderState.DISPATCH_VALIDATED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_DELIVERY).
                                expectState(OrderState.DELIVERY_COMPLETED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_to_dispatch_refuse() throws Exception {
        fromStock(false);
        toDispatch(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.RECEIVE).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.START_DISPATCH).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_cancel() throws Exception {
        fromStock(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.RECEIVE).expectState(OrderState.RECEIVED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.CANCEL_RECEIVE).expectState(OrderState.TO_RECEIVE)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_to_stock() throws Exception {
        fromStock(false);
        toStock(true);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                         .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.RECEIVE).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_RECEIPT).
                                expectState(OrderState.TO_STOCK)
                        .and()
                        .step().
                                sendEvent(OrderEvent.FIXME).
                                expectState(OrderState.DELIVERY_COMPLETED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_receipt_to_stock_refuse() throws Exception {
        fromStock(false);
        toStock(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                                expectState(OrderState.INITIAL)
                        .and()
                        .step().
                                sendEvent(OrderEvent.INIT).
                                expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                                sendEvent(OrderEvent.RECEIVE).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .step().
                                sendEvent(OrderEvent.VALIDATE_RECEIPT).
                                expectState(OrderState.RECEIVED)
                        .and()
                        .build();
        plan.test();
    }


    @Test
    public void test_cancel_dispatch_jit() throws Exception {
        fromStock(false);
        toDispatch(true);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                        expectState(OrderState.INITIAL)
                        .and()
                        .step().
                        sendEvent(OrderEvent.INIT).
                        expectState(OrderState.TO_RECEIVE)
                        .and()
                        .step().
                        sendEvent(OrderEvent.RECEIVE).
                        expectState(OrderState.RECEIVED)
                        .and()
                        .step().
                        sendEvent(OrderEvent.START_DISPATCH).
                        expectState(OrderState.DISPATCH_IN_PROGRESS)
                        .and()
                        .step().
                        sendEvent(OrderEvent.CANCEL_ALL_PASSES).
                        expectState(OrderState.RECEIVED)
                        .and()
                        .build();
        plan.test();
    }

    @Test
    public void test_cancel_dispatch_from_stock() throws Exception {
        fromStock(true);
        toDispatch(false);

        StateMachineTestPlan<OrderState, OrderEvent> plan =
                StateMachineTestPlanBuilder.<OrderState, OrderEvent>builder()
                        .stateMachine(orderMachineFactory.getStateMachine(What.ORDER.name()))
                        .step().
                        expectState(OrderState.INITIAL)
                        .and()
                        .step().
                        sendEvent(OrderEvent.INIT).
                        expectState(OrderState.TO_PREPARE)
                        .and()
                        .step().
                        sendEvent(OrderEvent.VALIDATE_PREPARATION).
                        expectState(OrderState.AWAITING_PREPARATION)
                        .and()
                        .step().
                        sendEvent(OrderEvent.START_PREPARATION).
                        expectState(OrderState.DISPATCH_IN_PROGRESS)
                        .and()
                        .step().
                        sendEvent(OrderEvent.CANCEL_ALL_PASSES).
                        expectState(OrderState.DISPATCH_IN_PROGRESS)
                        .and()
                        .build();
        plan.test();
    }

}