package fr.demo.state.api;

import fr.demo.state.order.OrderEvent;
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
public class NotifyApi {

    private final OrderStatePersist orderStatePersist;

    @Autowired
    public NotifyApi(OrderStatePersist orderStatePersist) {
        this.orderStatePersist = orderStatePersist;
    }

    @PutMapping(value = "/order", produces = "application/json")
    @ApiOperation(value = "Change state of an Order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully change Order state"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "An error occurred during process,please check the logs")
    })
    public void notifyOrder(@RequestParam("code") String code,
                            @RequestParam("event") OrderEvent event) {
        orderStatePersist.change(code, event);
    }
}
