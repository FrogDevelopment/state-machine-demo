package fr.demo.state.preparation.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PreparationDaoImpl implements PreparationDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public List<String> getOrderCodesOfPreparation(String code) {
        // get all orders associated to the Preparation
        String sql = "SELECT spo.ORDER_PROVIDER_CODE" +
                     " FROM STOCK_PREPARATION sp" +
                     " INNER JOIN STOCK_PREPARATION_ORDER spo ON spo.STOCK_PREPARATION_ID = sp.STOCK_PREPARATION_ID" +
                     " WHERE sp.STOCK_PREPARATION_CODE = :code";

        MapSqlParameterSource parameters = new MapSqlParameterSource("code", code);

        return jdbcTemplate.queryForList(sql, parameters, String.class);
    }

}
