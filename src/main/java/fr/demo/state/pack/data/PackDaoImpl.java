package fr.demo.state.pack.data;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
@Transactional(propagation = Propagation.MANDATORY, readOnly = true)
public class PackDaoImpl implements PackDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PackDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String create(String orderCode) {
        String packCode = "PACK_" + orderCode;

        jdbcTemplate.update("INSERT into DEMO_PACK (CODE, STATE, ORDER_CODE) values (:code, 'INITIAL', :orderCode)", Map.of("code", packCode, "orderCode", orderCode));

        return packCode;
    }

    @Override
    public String getOrderCode(String packCode) {
        return jdbcTemplate.queryForObject("select ORDER_CODE from DEMO_PAC where code = :code", Map.of("code", packCode), String.class);
    }

}
