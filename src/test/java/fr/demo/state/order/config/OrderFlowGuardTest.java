package fr.demo.state.order.config;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.data.OrderDao;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.support.DefaultStateContext;

@RunWith(MockitoJUnitRunner.class)
public class OrderFlowGuardTest {

    private static final String CODE = "code";

    @InjectMocks
    private OrderFlowGuard orderFlowGuard = new OrderFlowGuard("JIT");

    @Mock
    private OrderDao orderDao;
    @Mock
    private DefaultStateContext<OrderState, OrderEvent> stateContext;
    @Mock
    private Message message;
    @Mock
    private MessageHeaders headers;

    @Before
    public void setup() {
        Mockito.doReturn(message).when(stateContext).getMessage();
        Mockito.doReturn(headers).when(message).getHeaders();
    }

    @Test
    public void test_evaluate_no_header() {
        // MOCK
        Mockito.doReturn(false).when(headers).containsKey(AbstractStatePersist.HN_CODE);

        try {
            // CALL
            orderFlowGuard.evaluate(stateContext);
            Assertions.failBecauseExceptionWasNotThrown(StateMachineException.class);
        } catch (StateMachineException e) {
            // ASSERTIONS
            Assertions.assertThat(e.getMessage()).isEqualTo("Unable to get request on header to evaluate ORDER");
        }
    }

    @Test
    public void test_evaluate_true() {
        // MOCK
        Mockito.doReturn(true).when(headers).containsKey(AbstractStatePersist.HN_CODE);

        Mockito.doReturn(CODE).when(headers).get(AbstractStatePersist.HN_CODE, String.class);

        Mockito.doReturn("JIT").when(orderDao).getOrderType(CODE);

        // CALL
        boolean toDispatch = orderFlowGuard.evaluate(stateContext);

        // ASSERTIONS
        Mockito.verify(orderDao, Mockito.times(1)).getOrderType(CODE);
        Assertions.assertThat(toDispatch).isTrue();
    }

    @Test
    public void test_evaluate_false() {
        // MOCK
        Mockito.doReturn(true).when(headers).containsKey(AbstractStatePersist.HN_CODE);

        Mockito.doReturn(CODE).when(headers).get(AbstractStatePersist.HN_CODE, String.class);

        Mockito.doReturn("TO_STOCK").when(orderDao).getOrderType(CODE);

        // CALL
        boolean toDispatch = orderFlowGuard.evaluate(stateContext);

        // ASSERTIONS
        Mockito.verify(orderDao, Mockito.times(1)).getOrderType(CODE);
        Assertions.assertThat(toDispatch).isFalse();
    }
}