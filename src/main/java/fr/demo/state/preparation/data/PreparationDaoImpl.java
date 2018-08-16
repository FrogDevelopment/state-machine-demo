package fr.demo.state.preparation.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PreparationDaoImpl implements PreparationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public PreparationDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getOrderCode(String code) {
        String sql = "SELECT ORDER_CODE FROM DEMO_PACKAGE WHERE CODE = :code";

        MapSqlParameterSource parameters = new MapSqlParameterSource("code", code);

        return jdbcTemplate.queryForObject(sql, parameters, String.class);
    }

}
