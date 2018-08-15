package fr.demo.state.order.data;

import fr.demo.state.order.Flow;

public interface OrderDao{

    Flow getOrderFlow(String orderCode);
}
