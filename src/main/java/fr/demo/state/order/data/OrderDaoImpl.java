package fr.demo.state.order.data;

import fr.demo.state.order.Flow;
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
    public Flow getOrderFlow(String orderCode) {
        String sql = "SELECT opv.flow FROM DEMO_ORDER WHERE code = :orderCode";

        MapSqlParameterSource parameters = new MapSqlParameterSource("orderCode", orderCode);

        return jdbcTemplate.queryForObject(sql, parameters, (rs, i) -> Flow.valueOf(rs.getString("flow")));
    }
}
