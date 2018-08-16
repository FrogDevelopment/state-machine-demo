package fr.demo.state.order.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Transactional(propagation = Propagation.MANDATORY, readOnly = true)
public class OrderDaoImpl implements OrderDao {

    protected final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Map<String, String> order) {
        jdbcTemplate.update("INSERT into DEMO_ORDER (CODE, STATE, UPDATE_DATETIME) values (:code, :state, getDate())", order);
    }

    @Override
    public Map<String, Object> get(String code) {
        return jdbcTemplate.queryForMap("SELECT * FROM DEMO_ORDER where CODE = :code", new MapSqlParameterSource("code", code));
    }

    @Override
    public List<Map<String, Object>> getAll() {
        return jdbcTemplate.queryForList("SELECT * FROM DEMO_ORDER", new EmptySqlParameterSource());
    }

    @Override
    public String getOrderType(String code) {
        String sql = "SELECT ORDER_TYPE FROM DEMO_ORDER WHERE CODE = :code";

        MapSqlParameterSource parameters = new MapSqlParameterSource("code", code);

        return jdbcTemplate.queryForObject(sql, parameters, String.class);
    }
}
