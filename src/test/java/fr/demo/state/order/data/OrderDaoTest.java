package fr.demo.state.order.data;

import fr.demo.state.order.Flow;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(propagation = Propagation.REQUIRED)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class OrderDaoTest {

    @Autowired
    private OrderDao orderDao;

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void test_getOrderFlow() {
        jdbcTemplate.update("INSERT INTO [ORDER_PROVIDER]" +
                " ([ORDER_PROVIDER_CODE], [PROVIDER_ID], [WAREHOUSE_CODE], [STATUS], [FLOW])" +
                " VALUES ('ORDER_X', 1, 100, 'INITIAL', 'JIT')");

        Flow flow = orderDao.getOrderFlow("ORDER_X");

        Assertions.assertThat(flow).isEqualTo(Flow.JIT);
    }

}