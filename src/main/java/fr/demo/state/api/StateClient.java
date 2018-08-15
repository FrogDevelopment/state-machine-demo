package fr.demo.state.api;

import fr.demo.state.order.OrderEvent;
import fr.demo.state.order.OrderState;
import fr.demo.state.order.data.OrderStatePersist;
import fr.demo.state.preparation.PreparationEvent;
import fr.demo.state.preparation.PreparationState;
import fr.demo.state.preparation.data.PreparationStatePersist;
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
public class StateClient {

    private final PreparationStatePersist preparationStatePersist;

    private final OrderStatePersist orderStatePersist;

    @Autowired
    public StateClient(PreparationStatePersist preparationStatePersist, OrderStatePersist orderStatePersist) {
        this.preparationStatePersist = preparationStatePersist;
        this.orderStatePersist = orderStatePersist;
    }

    @PutMapping(value = "/preparation", produces = "application/json")
    @ApiOperation(value = "Change state of a Preparation", response = PreparationState.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully change Preparation state"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public PreparationState changePreparationState(@RequestParam("code") String code,
                                                   @RequestParam("event") PreparationEvent event) {
        return preparationStatePersist.change(code, event);
    }

    @PutMapping(value = "/order", produces = "application/json")
    @ApiOperation(value = "Change state of an Order", response = PreparationState.class)
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
