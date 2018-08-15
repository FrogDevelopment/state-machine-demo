package fr.demo.state.order.data;

import fr.demo.state.order.Flow;

import java.util.List;
import java.util.Map;

public interface OrderDao{

    Map<String, Object> get(String code);

    List<Map<String, Object>> getAll();

    void create(Map<String, String> order);

    Flow getOrderFlow(String orderCode);
}
