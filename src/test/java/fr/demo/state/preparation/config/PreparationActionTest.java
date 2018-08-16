package fr.demo.state.preparation.config;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.data.OrderStatePersist;
import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import fr.demo.state.preparation.data.PreparationDao;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.support.DefaultStateContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PreparationActionTest {

    private static final String CODE = "code";

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

    @Before
    public void setup() {
        doReturn(message).when(stateContext).getMessage();
        doReturn(headers).when(message).getHeaders();
    }

    @Test
    public void test_execute_no_header() {
        // given
        doReturn(false).when(headers).containsKey(AbstractStatePersist.HN_CODE);

        // when
        Throwable catchThrowable = Assertions.catchThrowable(() -> preparationAction.execute(stateContext));

        // then
        assertThat(catchThrowable).isInstanceOf(StateMachineException.class).hasMessageContaining(OrderEvent.VALIDATE_PREPARATION.name());
    }

    @Test
    public void test_execute_header() {
        // given
        doReturn(true).when(headers).containsKey(AbstractStatePersist.HN_CODE);

        doReturn(CODE).when(headers).get(AbstractStatePersist.HN_CODE, String.class);

        doReturn("order_1").when(preparationDao).getOrderCode(CODE);

        // when
        preparationAction.execute(stateContext);

        // then
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);

        verify(orderStatePersist).change(codeCaptor.capture(), eventCaptor.capture());
        String code = codeCaptor.getValue();
        assertThat(code).isEqualTo("order_1");

        OrderEvent event = eventCaptor.getValue();
        assertThat(event).isEqualTo(OrderEvent.VALIDATE_PREPARATION);
    }

}