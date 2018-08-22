package fr.demo.state.order.data;

import fr.demo.state.common.AbstractStatePersist;
import fr.demo.state.common.What;
import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderStatePersist extends AbstractStatePersist<OrderState, OrderEvent> {

    public OrderStatePersist(@Autowired @Qualifier(value = "orderMachine") StateMachineFactory<OrderState, OrderEvent> stateMachine) {
        super(stateMachine, What.ORDER);
    }

    @Override
    protected OrderState getState(String key) {
        String sql = "SELECT STATE FROM DEMO_ORDER WHERE CODE = :code";
        MapSqlParameterSource parameters = new MapSqlParameterSource("code", key);

        String status = jdbcTemplate.queryForObject(sql, parameters, String.class);

        return OrderState.valueOf(status);
    }

    @Override
    protected void updateState(String key, OrderState state) {
        String sql = "UPDATE DEMO_ORDER SET STATE = :state WHERE CODE = :code";
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("code", key);
        parameters.addValue("state", state.name());

        jdbcTemplate.update(sql, parameters);
    }
}
