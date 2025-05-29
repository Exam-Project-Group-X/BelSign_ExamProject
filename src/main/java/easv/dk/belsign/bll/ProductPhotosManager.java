package easv.dk.belsign.bll;

import easv.dk.belsign.be.ProductPhotos;
import easv.dk.belsign.dal.web.ProductPhotosDAO;

import java.sql.SQLException;
import java.util.Map;

public class ProductPhotosManager {
    private final ProductPhotosDAO photosDAO = new ProductPhotosDAO();

    public void approvePhoto(int orderId, String angle) throws SQLException {
        photosDAO.approvePhoto(orderId, angle);
    }

    public void rejectPhoto(int orderId, String angle, String comment) throws SQLException {
        photosDAO.rejectPhoto(orderId, angle, comment);
    }

    public void deletePhoto(int orderId, String angle) throws SQLException {
        photosDAO.deletePhoto(orderId, angle);
    }

    public Map<String, byte[]> getPhotosByOrderId(int orderId) throws SQLException {
        return photosDAO.getPhotosByOrderId(orderId);
    }

    public Map<String, ProductPhotos> getDetailedPhotosByOrderId(int orderId) throws SQLException {
        return photosDAO.getDetailedPhotosByOrderId(orderId);
    }

    public Map<String, String> getPhotoStatusByOrderId(int orderId) throws SQLException {
        return photosDAO.getPhotoStatusByOrderId(orderId);
    }

    public void upsertCapturedPhoto(int orderId, String photoAngle, byte[] photoData, String operatorName) throws SQLException {
        photosDAO.upsertCapturedPhoto(orderId, photoAngle, photoData, operatorName);
    }
}
