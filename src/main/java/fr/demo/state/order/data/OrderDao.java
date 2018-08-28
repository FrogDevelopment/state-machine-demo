package fr.demo.state.order.data;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    List<Map<String, Object>> getAll();

    Map<String, Object> getOrder(String orderCode);
}
