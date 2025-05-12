package easv.dk.belsign.gui.models;


import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;

import java.util.List;


public class QAEmployeeModel {

    private final OrderManager orderManager = new OrderManager();


    public List<Order> getAllOrders() throws Exception {
        return orderManager.getAllOrders();
    }

    public void setOrderToCompleted(int orderId) throws Exception {
        orderManager.updateOrderToComplete(orderId);
    }

    }
