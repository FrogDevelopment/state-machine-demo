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

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(propagation = Propagation.REQUIRED)
@ContextConfiguration(locations = {"classpath:application-data-test.xml"})
public class PreparationStatePersistTest {

    @Autowired
    private PreparationStatePersist preparationStatePersist;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void before() {
        jdbcTemplate.update("INSERT INTO DEMO_PACKAGE (CODE, STATE, UPDATE_DATETIME, ORDER_CODE) VALUES ('PACKAGE_X', 'INITIAL', getDate(), 'ORDER_X')");
    }

    @Test
    public void test_getState() {
        PreparationState state = preparationStatePersist.getState("PACKAGE_X");

        Assertions.assertThat(state).isEqualTo(PreparationState.INITIAL);
    }

    @Test
    public void test_updateState() {
        preparationStatePersist.updateState("PACKAGE_X", PreparationState.IN_PROGRESS);

        PreparationState state = preparationStatePersist.getState("PACKAGE_X");

        Assertions.assertThat(state).isEqualTo(PreparationState.IN_PROGRESS);
    }

}