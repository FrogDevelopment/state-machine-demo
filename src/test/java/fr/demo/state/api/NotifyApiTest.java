package fr.demo.state.api;

import fr.demo.state.common.What;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.data.OrderStatePersist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class NotifyApiTest {

    @InjectMocks
    private NotifyApi notifyApi;

    @Mock
    private OrderStatePersist orderStatePersist;

    @Test
    public void test_changeOrderState() throws Exception {
        // given
        String code = "CODE";

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(notifyApi)
                .build();

        doNothing().when(orderStatePersist).change(anyString(), any(OrderEvent.class));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/notify/" + What.ORDER.name().toLowerCase());

        requestBuilder.param("code", code)
                .param("event", OrderEvent.RECEIPT.name());

        mockMvc.perform(requestBuilder)
//               .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        // then
        ArgumentCaptor<String> codeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderEvent> eventArgumentCaptor = ArgumentCaptor.forClass(OrderEvent.class);

        verify(orderStatePersist, times(1)).change(codeArgumentCaptor.capture(), eventArgumentCaptor.capture());

        assertThat(codeArgumentCaptor.getValue()).isEqualTo(code);
        assertThat(eventArgumentCaptor.getValue()).isEqualTo(OrderEvent.RECEIPT);

        verifyNoMoreInteractions(orderStatePersist);
    }
}