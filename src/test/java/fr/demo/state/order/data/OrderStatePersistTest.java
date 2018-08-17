package fr.demo.state.order.data;

import fr.demo.state.order.OrderState;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(propagation = Propagation.REQUIRED)
@ContextConfiguration(locations = {"classpath:application-data-test.xml"})
public class OrderStatePersistTest {

    @Autowired
    private OrderStatePersist orderStatePersist;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void before() {
        jdbcTemplate.update("INSERT INTO DEMO_ORDER (CODE, STATE, UPDATE_DATETIME) VALUES ('ORDER_X', 'INITIAL', getDate())");
    }

    @Test
    public void test_getState() {
        OrderState state = orderStatePersist.getState("ORDER_X");

        Assertions.assertThat(state).isEqualTo(OrderState.INITIAL);
    }

    @Test
    public void test_updateState() {
        orderStatePersist.updateState("ORDER_X", OrderState.DRAFT);

        OrderState state = orderStatePersist.getState("ORDER_X");

        Assertions.assertThat(state).isEqualTo(OrderState.DRAFT);
    }

}