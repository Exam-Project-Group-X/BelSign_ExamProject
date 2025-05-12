package easv.dk.belsign.bll;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.web.OrderDAO;
import javafx.collections.ObservableList;

import java.util.List;

public class OrderManager {
    private final OrderDAO orderDAO = new OrderDAO();

    public List<Order> getAllOrders() throws Exception {
        return orderDAO.getAllOrders();
    }

    // Get all 'Pending' Orders
    public ObservableList<Order> getAllPendingOrders() {
        return orderDAO.getAllPendingOrders();
    }

    public void updateOrderToPending(Order order) throws Exception{
        orderDAO.updateOrderStatusToPending(order.getOrderID());
    }

    public void updateOrderToComplete(int orderId) {
        orderDAO.updateOrderStatusToComplete(orderId);
    }
}
