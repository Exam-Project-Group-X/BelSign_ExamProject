package easv.dk.belsign.bll;

import easv.dk.belsign.dal.web.ProductPhotosDAO;

import java.sql.SQLException;

public class ProductPhotosManager {
    private final ProductPhotosDAO photosDAO = new ProductPhotosDAO();

    public void approvePhoto(int orderId, String angle) throws SQLException {
        photosDAO.approvePhoto(orderId, angle);
    }

    public void rejectPhoto(int orderId, String angle) throws SQLException {
        photosDAO.rejectPhoto(orderId, angle);
    }
}
