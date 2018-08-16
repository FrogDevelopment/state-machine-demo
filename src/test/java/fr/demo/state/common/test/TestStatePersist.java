package fr.demo.state.common.test;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.common.What;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@SuppressWarnings("deprecation")
@Transactional(propagation = Propagation.MANDATORY)
public class TestStatePersist extends AbstractStatePersist<TestState, TestEvent> {

    public TestStatePersist(@Autowired @Qualifier(value = "testMachine") StateMachineFactory<TestState, TestEvent> stateMachine) {
        super(stateMachine, What.TEST);
    }

    @Override
    protected TestState getState(String key) {
        String sql = "SELECT TEST_STATE FROM TEST_TABLE WHERE TEST_CODE = :code";
        MapSqlParameterSource parameters = new MapSqlParameterSource("code", key);

        String status = jdbcTemplate.queryForObject(sql, parameters, String.class);

        return TestState.valueOf(status);
    }

    @Override
    protected void updateState(String key, TestState state) {
        String sql = "UPDATE TEST_TABLE SET TEST_STATE =:state WHERE TEST_CODE=:code";
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("code", key);
        parameters.addValue("state", state.name());

        jdbcTemplate.update(sql, parameters);
    }
    
}
