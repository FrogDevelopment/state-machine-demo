package fr.demo.state.api;

import fr.demo.state.order.data.OrderDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
@Api(value = "data", description = "Operations to create & visualize entities")
public class DemoApi {

    private final OrderDao orderDao;

    @Autowired
    public DemoApi(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @GetMapping(value = "/orders", produces = "application/json")
    @ApiOperation(value = "Get all Orders", response = List.class)
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Map<String, Object>> getAllOrders() {
        return orderDao.getAll();
    }

    @GetMapping(value = "/order", produces = "application/json")
    @ApiOperation(value = "Get all Orders", response = Map.class)
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> getOrder(@RequestParam String orderCode) {
        return orderDao.getOrder(orderCode);
    }

}
