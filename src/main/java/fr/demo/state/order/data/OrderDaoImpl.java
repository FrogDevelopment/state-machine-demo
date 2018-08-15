package fr.demo.state.order.data;

import fr.demo.state.order.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY, readOnly = true)
public class OrderDaoImpl implements OrderDao {

    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Flow getOrderFlow(String orderCode) {
        String sql = "SELECT opv.flow FROM order_provider opv WHERE opv.order_provider_code = :orderCode";

        MapSqlParameterSource parameters = new MapSqlParameterSource("orderCode", orderCode);

        return jdbcTemplate.queryForObject(sql, parameters, (rs, i) -> Flow.valueOf(rs.getString("flow")));
    }
}
