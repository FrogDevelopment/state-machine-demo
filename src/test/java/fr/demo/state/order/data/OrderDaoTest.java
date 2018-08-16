package fr.demo.state.order.data;

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
@ContextConfiguration(locations = {"classpath:application-data-test.xml"})
public class OrderDaoTest {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test_getOrderFlow() {
        jdbcTemplate.update("INSERT INTO DEMO_ORDER (CODE, STATE, UPDATE_DATETIME, ORDER_TYPE) VALUES ('ORDER_X', 'INITIAL', getDate(), 'TEST')");

        String type = orderDao.getOrderType("ORDER_X");

        Assertions.assertThat(type).isEqualTo("TEST");
    }

}