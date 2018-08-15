package fr.demo.state.preparation.data;

import fr.demo.state.preparation.PreparationState;
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

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(propagation = Propagation.REQUIRED)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class PreparationStatePersistTest {

    @Autowired
    private PreparationStatePersist preparationStatePersist;

    @Autowired
    private PreparationDao preparationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeTests() {
        int nbUpdated = jdbcTemplate.update("INSERT INTO STOCK_PREPARATION" +
                " (STOCK_PREPARATION_CODE, WAREHOUSE_FROM, WAREHOUSE_TO, PRIORITY, CREATED_BY, AFFECTED_TO, STATUS, LAST_UPDATE_DATETIME)" +
                " VALUES ('PREP_1','100-1','100-1','0','t.est', '','CREATED', getdate())");
        if (nbUpdated == 0) {
            throw new IllegalStateException("No insert !!");
        }
    }

    @Test
    public void test_getState() {
        PreparationState state = preparationStatePersist.getState("PREP_1");

        Assertions.assertThat(state).isEqualTo(PreparationState.CREATED);
    }

    @Test
    public void test_updateState() {
        preparationStatePersist.updateState("PREP_1", PreparationState.IN_PROGRESS);

        PreparationState state = preparationStatePersist.getState("PREP_1");

        Assertions.assertThat(state).isEqualTo(PreparationState.IN_PROGRESS);
    }

    @Test
    public void test_getOrderCodesOfPreparation() {
        jdbcTemplate.update("INSERT INTO STOCK_PREPARATION_ORDER" +
                " (STOCK_PREPARATION_ID, ORDER_PROVIDER_CODE)" +
                " SELECT sp.STOCK_PREPARATION_ID, 'ORDER_1'" +
                " FROM STOCK_PREPARATION sp WHERE sp.STOCK_PREPARATION_CODE = 'PREP_1'");

        jdbcTemplate.update("INSERT INTO STOCK_PREPARATION_ORDER" +
                " (STOCK_PREPARATION_ID, ORDER_PROVIDER_CODE)" +
                " SELECT sp.STOCK_PREPARATION_ID, 'ORDER_2'" +
                " FROM STOCK_PREPARATION sp WHERE sp.STOCK_PREPARATION_CODE = 'PREP_1'");

        jdbcTemplate.update("INSERT INTO STOCK_PREPARATION_ORDER" +
                " (STOCK_PREPARATION_ID, ORDER_PROVIDER_CODE)" +
                " SELECT sp.STOCK_PREPARATION_ID, 'ORDER_3'" +
                " FROM STOCK_PREPARATION sp WHERE sp.STOCK_PREPARATION_CODE = 'PREP_1'");

        jdbcTemplate.update("INSERT INTO STOCK_PREPARATION_ORDER" +
                " (STOCK_PREPARATION_ID, ORDER_PROVIDER_CODE)" +
                " SELECT sp.STOCK_PREPARATION_ID, 'ORDER_4'" +
                " FROM STOCK_PREPARATION sp WHERE sp.STOCK_PREPARATION_CODE = 'PREP_1'");


        List<String> orderCodes = preparationDao.getOrderCodesOfPreparation("PREP_1");

        Assertions.assertThat(orderCodes).hasSize(4);
        Assertions.assertThat(orderCodes).contains("ORDER_1", "ORDER_2", "ORDER_3", "ORDER_4");
    }

}