package fr.demo.state.pack.data;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.common.What;
import fr.demo.state.pack.PackEvent;
import fr.demo.state.pack.PackState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

@Component
public class PackStatePersist extends AbstractStatePersist<PackState, PackEvent> {

    public PackStatePersist(@Autowired @Qualifier(value = "packMachine") StateMachineFactory<PackState, PackEvent> stateMachine) {
        super(stateMachine, What.PACK);
    }

    @Override
    protected PackState getState(String key) {
        String sql = "SELECT STATE FROM DEMO_PACK WHERE CODE = :code";
        MapSqlParameterSource parameters = new MapSqlParameterSource("code", key);

        String status = jdbcTemplate.queryForObject(sql, parameters, String.class);

        return PackState.valueOf(status);
    }

    @Override
    protected void updateState(String key, PackState state) {
        String sql = "UPDATE DEMO_PACK SET STATE = :state WHERE CODE = :code";
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("code", key);
        parameters.addValue("state", state.name());

        jdbcTemplate.update(sql, parameters);
    }
}
