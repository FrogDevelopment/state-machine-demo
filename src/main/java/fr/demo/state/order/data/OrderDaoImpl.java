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

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> orders = jdbcTemplate.queryForList("SELECT * FROM DEMO_ORDER", new EmptySqlParameterSource());

        orders.forEach(order -> {
            Map<String, Object> params = Map.of("orderCode", order.get("CODE"));

            List<Map<String, Object>> products = jdbcTemplate.queryForList("SELECT dp.* FROM DEMO_PRODUCT dp inner join PRODUCT_ORDER po on po.PRODUCT_CODE = dp.CODE and po.ORDER_CODE = :orderCode", params);
            order.put("PRODUCTS", products);

            List<Map<String, Object>> packs = jdbcTemplate.queryForList("SELECT * FROM DEMO_PACK where ORDER_CODE = :orderCode", params);
            order.put("PACKS", packs);
        });

        return orders;
    }

    @Override
    public boolean hasOnlyNumericProduct(String orderCode) {
        String sql = "SELECT" +
                " case when count(dp.CODE) = sum(case when dp.IS_NUMERIC = true " +
                "                                               then 1 " +
                "                                               else 0 " +
                "                                       end)" +
                "       then 1" +
                "       else 0" +
                "      end" +
                " FROM DEMO_PRODUCT dp" +
                " INNER JOIN PRODUCT_ORDER po on po.PRODUCT_CODE = DP.CODE and po.ORDER_CODE = :orderCode";

        MapSqlParameterSource paramSource = new MapSqlParameterSource("orderCode", orderCode);

        return jdbcTemplate.queryForObject(sql, paramSource, Boolean.class);
    }
}
