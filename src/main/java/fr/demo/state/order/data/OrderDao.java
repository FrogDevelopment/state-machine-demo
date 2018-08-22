package fr.demo.state.order.data;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    Map<String, Object> get(String code);

    List<Map<String, Object>> getAll();

    void create(Map<String, String> order);

    boolean hasOnlyNumericProduct(String orderCode);
}
