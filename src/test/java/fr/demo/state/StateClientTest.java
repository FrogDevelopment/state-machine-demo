package fr.demo.state;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.common.DemoEvent;
import fr.demo.state.common.DemoState;
import fr.demo.state.common.What;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.data.OrderStatePersist;
import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import fr.demo.state.preparation.data.PreparationStatePersist;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class StateClientTest {

    @InjectMocks
    private StateClient stateClient;

    @Mock
    private PreparationStatePersist preparationStatePersist;

    @Mock
    private OrderStatePersist orderStatePersist;

    // ************************ \\
    //         MOCK MVC         \\
    // ************************ \\

    // cf https://memorynotfound.com/unit-test-spring-mvc-rest-service-junit-mockito/
    private MockMvc mockMvc;

    private static final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
    }

    private static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(stateClient)
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }

    // ************************ \\
    //         generic          \\
    // ************************ \\

    private void assert_changeState(What what,
                                    DemoEvent event,
                                    DemoState expectedState,
                                    AbstractStatePersist mock) throws Exception {
        assert_changeState(what.name().toLowerCase(), event, expectedState, mock);
    }

    @SuppressWarnings("unchecked")
    private void assert_changeState(String what,
                                    DemoEvent event,
                                    DemoState expectedState,
                                    AbstractStatePersist mock) throws Exception {
        // data
        String code = "CODE";

        // mock
        doReturn(expectedState).when(mock).change(anyString(), any(DemoEvent.class));

        // call
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/notify/" + what);

        requestBuilder.param("code", code)
                .param("event", event.name());

        mockMvc.perform(requestBuilder)
//               .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().json(asJsonString(expectedState)))
        ;

        // assertions
        ArgumentCaptor<String> codeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DemoEvent> eventArgumentCaptor = ArgumentCaptor.forClass(DemoEvent.class);
        verify(mock, times(1)).change(codeArgumentCaptor.capture(), eventArgumentCaptor.capture());
        assertThat(codeArgumentCaptor.getValue()).isEqualTo(code);
        assertThat(eventArgumentCaptor.getValue()).isEqualTo(event);

        verifyNoMoreInteractions(mock);
    }

    // ************************ \\
    //       PREPARATION        \\
    // ************************ \\

    @Test
    public void test_changePreparationState() throws Exception {
        // call & assert
        assert_changeState(What.PREPARATION, PreparationEvent.CLOSE, PreparationState.CLOSED, preparationStatePersist);
    }

    // ************************ \\
    //           ORDER          \\
    // ************************ \\

    @Test
    public void test_changeOrderState() throws Exception {
        // call & assert
        assert_changeState(What.ORDER, OrderEvent.RECEIVE, OrderState.RECEIVED, orderStatePersist);
    }

}