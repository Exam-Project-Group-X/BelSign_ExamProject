package easv.dk.belsign.gui.controllers.QAEmployee.model;


import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;

import java.util.List;


public class QAmodel {

    private final OrderManager orderManager = new OrderManager();

    public int createOrder(Order order) throws Exception {
        return orderManager.createOrder(order);
    }

    public List<Order> getAllOrders() throws Exception {
        return orderManager.getAllOrders();
    }
}
