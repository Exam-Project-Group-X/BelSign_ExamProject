package easv.dk.belsign.gui.QAControllers.model;


import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;


public class QAmodel {

    private final OrderManager orderManager = new OrderManager();

    public int createOrder(Order order) throws Exception {
        return orderManager.createOrder(order);
    }

}
