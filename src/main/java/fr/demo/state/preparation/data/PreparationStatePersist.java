package fr.demo.state.preparation.data;

import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Repository;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.common.What;

@Repository
public class PreparationStatePersist extends AbstractStatePersist<PreparationState, PreparationEvent> {

    public PreparationStatePersist(@Autowired @Qualifier(value = "preparationMachine") StateMachineFactory<PreparationState, PreparationEvent> stateMachine) {
        super(stateMachine, What.PREPARATION);
    }

    @Override
    public PreparationState getState(String key) {
        String sql = "SELECT STATUS FROM STOCK_PREPARATION WHERE STOCK_PREPARATION_CODE = :code";
        MapSqlParameterSource parameters = new MapSqlParameterSource("code", key);

        String status = jdbcTemplate.queryForObject(sql, parameters, String.class);

        return PreparationState.valueOf(status);
    }

    @Override
    public void updateState(String key, PreparationState state) {
        String sql = "UPDATE STOCK_PREPARATION SET STATUS = :state WHERE STOCK_PREPARATION_CODE = :code";
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("code", key);
        parameters.addValue("state", state.name());

        jdbcTemplate.update(sql, parameters);
    }


}
