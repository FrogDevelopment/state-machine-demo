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
    public void create(Map<String, String> pack) {
        jdbcTemplate.update("INSERT into DEMO_PACK (CODE, STATE, ORDER_CODE) values (:code, :state, :order_code)", pack);
    }

}
