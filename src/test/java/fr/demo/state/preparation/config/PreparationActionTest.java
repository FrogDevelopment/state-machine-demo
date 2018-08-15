package fr.demo.state.preparation.config;

import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import fr.demo.state.preparation.config.PreparationAction;
import fr.demo.state.preparation.data.PreparationDao;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.support.DefaultStateContext;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.data.OrderStatePersist;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(MockitoJUnitRunner.class)
public class PreparationActionTest {

    public static final String CODE = "code";
    @InjectMocks
    private PreparationAction preparationAction = new PreparationAction(OrderEvent.VALIDATE_PREPARATION);

    @Mock
    private PreparationDao preparationDao;
    @Mock
    private OrderStatePersist orderStatePersist;
    @Mock
    private DefaultStateContext<PreparationState, PreparationEvent> stateContext;
    @Mock
    private Message message;
    @Mock
    private MessageHeaders headers;

    @Captor
    private ArgumentCaptor<String> codeCaptor;
    @Captor
    private ArgumentCaptor<OrderEvent> eventCaptor;

    @Before
    public void setup() {
        Mockito.doReturn(message).when(stateContext).getMessage();
        Mockito.doReturn(headers).when(message).getHeaders();
    }

    @Test
    public void test_execute_no_header() {
        // MOCK
        Mockito.doReturn(false).when(headers).containsKey(AbstractStatePersist.HN_CODE);

        try {
            // CALL
            preparationAction.execute(stateContext);
            Assertions.failBecauseExceptionWasNotThrown(StateMachineException.class);
        } catch (StateMachineException e) {
            // ASSERTIONS
            Assertions.assertThat(e.getMessage()).isEqualTo("Unable to get request on header to notify ORDERs with event " + OrderEvent.VALIDATE_PREPARATION);
        }
    }

    @Test
    public void test_execute_header() {
        // MOCK
        Mockito.doReturn(true).when(headers).containsKey(AbstractStatePersist.HN_CODE);

        Mockito.doReturn(CODE).when(headers).get(AbstractStatePersist.HN_CODE, String.class);

        List<String> orderCodes = Arrays.asList("order_1", "order_2", "order_3");
        Mockito.doReturn(orderCodes).when(preparationDao).getOrderCodesOfPreparation(CODE);

        // CALL
        preparationAction.execute(stateContext);

        // ASSERTIONS
        Mockito.verify(orderStatePersist, Mockito.times(orderCodes.size())).change(codeCaptor.capture(), eventCaptor.capture());
        List<String> codes = codeCaptor.getAllValues();
        IntStream.range(0, orderCodes.size()).forEach(i -> Assertions.assertThat(codes.get(i)).isEqualTo(orderCodes.get(i)));

        List<OrderEvent> events = eventCaptor.getAllValues();
        for (int i = 0; i < orderCodes.size(); i++) {
            Assertions.assertThat(events.get(i)).isEqualTo(OrderEvent.VALIDATE_PREPARATION);
        }
    }

}