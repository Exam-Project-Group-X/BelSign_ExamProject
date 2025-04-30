package easv.dk.belsign.bll;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.db.OrderDAODB;
public class OrderManager {

    private final OrderDAODB orderDAO = new OrderDAODB();


    public int createOrder(Order order) throws Exception{
        return orderDAO.createOrder(order);
    }

}
