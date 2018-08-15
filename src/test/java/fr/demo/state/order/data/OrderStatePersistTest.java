package fr.demo.state.order.data;

import fr.demo.state.order.OrderState;
import org.assertj.core.api.Assertions;
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
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class OrderStatePersistTest {

    @Autowired
    private OrderStatePersist orderStatePersist;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test_getState() {
        jdbcTemplate.update("INSERT INTO [ORDER_PROVIDER]" +
                " ([ORDER_PROVIDER_CODE], [PROVIDER_ID], [WAREHOUSE_CODE], [STATUS], [FLOW])" +
                " VALUES ('ORDER_X', 1, 100, 'INITIAL', 'JIT')");

        OrderState state = orderStatePersist.getState("ORDER_X");

        Assertions.assertThat(state).isEqualTo(OrderState.INITIAL);
    }

    @Test
    public void test_updateState() {
        jdbcTemplate.update("INSERT INTO [ORDER_PROVIDER]" +
                " ([ORDER_PROVIDER_CODE], [PROVIDER_ID], [WAREHOUSE_CODE], [STATUS], [FLOW])" +
                " VALUES ('ORDER_X', 1, 100, 'INITIAL', 'JIT')");

        orderStatePersist.updateState("ORDER_X", OrderState.AWAITING_PREPARATION);

        OrderState state = orderStatePersist.getState("ORDER_X");

        Assertions.assertThat(state).isEqualTo(OrderState.AWAITING_PREPARATION);
    }

}