package fr.demo.state.api;

import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.data.OrderStatePersist;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify")
@Api(value = "notify", description = "Operations to notify event to change entity state")
public class StateApi {

    private final OrderStatePersist orderStatePersist;

    @Autowired
    public StateApi(OrderStatePersist orderStatePersist) {
        this.orderStatePersist = orderStatePersist;
    }

    @PutMapping(value = "/order", produces = "application/json")
    @ApiOperation(value = "Change state of an Order", response = OrderState.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully change Order state"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public OrderState changeOrderState(@RequestParam("code") String code,
                                       @RequestParam("event") OrderEvent event) {
        return orderStatePersist.change(code, event);
    }
}
