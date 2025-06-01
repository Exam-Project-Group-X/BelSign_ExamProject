package easv.dk.belsign.gui.models;

import easv.dk.belsign.bll.ProductPhotosManager;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PhotosModel {
    ProductPhotosManager photosManager = new ProductPhotosManager();
    /** OrderID → number of photos */
    private final Map<Integer, Integer> countCache = new ConcurrentHashMap<>();

    public void approvePhoto(int orderId, String angle) throws SQLException {
        photosManager.approvePhoto(orderId, angle);
    }

    public void rejectPhoto(int orderId, String angle, String comment) throws SQLException {
        photosManager.rejectPhoto(orderId, angle, comment);
    }

    public void deletePhoto(int orderId, String angle) throws SQLException {
        photosManager.deletePhoto(orderId, angle);
    }

    public Map<String, byte[]> getPhotosByOrderId(int orderId) throws SQLException {
        return photosManager.getPhotosByOrderId(orderId);
    }

    public Map<String, String> getPhotoStatusByOrderId(int orderId) throws SQLException {
        return photosManager.getPhotoStatusByOrderId(orderId);
    }

    public int countPhotosForOrder(int orderId) throws SQLException {
        return photosManager.countPhotosForOrder(orderId);
    }

    public Map<Integer,Integer> countPhotosForOrders(Set<Integer> ids)
            throws SQLException {
        return photosManager.countPhotosForOrders(ids);
    }
}
