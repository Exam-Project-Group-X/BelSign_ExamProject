package easv.dk.belsign.bll;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.web.OrderDAO;
import javafx.collections.ObservableList;

import java.util.List;

public class OrderManager {
    private final OrderDAO orderDAO = new OrderDAO();

    public int createOrder(Order order) throws Exception{
        return orderDAO.createOrder(order);
    }

    public List<Order> getAllOrders() throws Exception {
        return orderDAO.getAllOrders();
    }

    // Get all 'New' Orders
    public ObservableList<Order> getAllNewOrders() {
        return orderDAO.getAllNewOrders();
    }
}
