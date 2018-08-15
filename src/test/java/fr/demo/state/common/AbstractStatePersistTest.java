package fr.demo.state.common;

import fr.demo.state.common.test.TestEvent;
import fr.demo.state.common.test.TestState;
import fr.demo.state.common.test.TestStatePersist;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(propagation = Propagation.REQUIRED)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
public class AbstractStatePersistTest {

    @Autowired
    private TestStatePersist statePersist;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void test_change_unknown_code() {
        // given
        // check entity not present on base
        int nb = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate.getJdbcTemplate(),  "TEST_TABLE", "TEST_CODE='XXX'");
        assertThat(nb).isEqualTo(0);

        // when
        Throwable catchThrowable = catchThrowable(() -> statePersist.change("XXX", TestEvent.START));

        // then
        assertThat(catchThrowable).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown code [XXX] for entity 'TEST'");
    }

    @Test
    public void test_change_refused() {
        // BEFORE CALL
        // check state on db
        TestState stateOnBase = getCurrentStateOnBase("CODE_3");
        assertThat(stateOnBase).isEqualTo(TestState.FINISHED);

        // CALL
        try {
            statePersist.change("CODE_3", TestEvent.START);
            failBecauseExceptionWasNotThrown(StateException.class);
        } catch (StateException e) {
            assertThat(e.getCode()).isEqualTo("CODE_3");
            assertThat(e.getEvent()).isEqualTo(TestEvent.START);
            assertThat(e.getCurrentState()).isEqualTo(TestState.FINISHED);
        }

        // AFTER CALL
        // check state on db haven't change
        stateOnBase = getCurrentStateOnBase("CODE_3");
        assertThat(stateOnBase).isEqualTo(TestState.FINISHED);
    }

    @Test
    public void test_change_accepted() {
        // BEFORE CALL
        // check state on db
        TestState stateOnBase = getCurrentStateOnBase("CODE_1");
        assertThat(stateOnBase).isEqualTo(TestState.CREATED);

        // CALL
        // call change of state
        TestState newState = statePersist.change("CODE_1", TestEvent.START);

        // check state returned
        assertThat(newState).isEqualTo(TestState.IN_PROGRESS);

        // AFTER CALL
        // check state on db have been updated
        stateOnBase = getCurrentStateOnBase("CODE_1");
        assertThat(stateOnBase).isEqualTo(TestState.IN_PROGRESS);
    }

    @Test
    public void test_change_full() {
        String codeEntity = "CODE_1";

        // BEFORE CALL
        // check state on db
        TestState stateOnBase = getCurrentStateOnBase(codeEntity);
        assertThat(stateOnBase).isEqualTo(TestState.CREATED);

        stateOnBase = getCurrentStateOnBase("CODE_2");
        assertThat(stateOnBase).isEqualTo(TestState.IN_PROGRESS);

        // CALL
        // call change of state
        statePersist.change(codeEntity, TestEvent.START);

        statePersist.change(codeEntity, TestEvent.FINISH);

        // AFTER CALL
        // check state on db have been updated
        stateOnBase = getCurrentStateOnBase(codeEntity);
        assertThat(stateOnBase).isEqualTo(TestState.FINISHED);
    }

    @Test
    public void test_change_update_multiple_entities() {

        // BEFORE CALL
        // check state on db
        TestState stateOnBase = getCurrentStateOnBase("CODE_1");
        assertThat(stateOnBase).isEqualTo(TestState.CREATED);

        // CALL
        // call change of state
        statePersist.change("CODE_1", TestEvent.START);

        statePersist.change("CODE_2", TestEvent.FINISH);

        statePersist.change("CODE_1", TestEvent.FINISH);

        // AFTER CALL
        // check state on db have been updated
        stateOnBase = getCurrentStateOnBase("CODE_1");
        assertThat(stateOnBase).isEqualTo(TestState.FINISHED);

        stateOnBase = getCurrentStateOnBase("CODE_2");
        assertThat(stateOnBase).isEqualTo(TestState.FINISHED);
    }

    private TestState getCurrentStateOnBase(String code) {
        String sql = "SELECT TEST_STATE FROM TEST_TABLE WHERE TEST_CODE = :code";
        MapSqlParameterSource params = new MapSqlParameterSource("code", code);
        String stateOnBase = jdbcTemplate.queryForObject(sql, params, String.class);

        return TestState.valueOf(stateOnBase);
    }
}