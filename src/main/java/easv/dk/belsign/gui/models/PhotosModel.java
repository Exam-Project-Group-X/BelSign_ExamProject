package easv.dk.belsign.gui.models;

import easv.dk.belsign.bll.ProductPhotosManager;

import java.sql.SQLException;
import java.util.Map;

public class PhotosModel {

    ProductPhotosManager photosManager = new ProductPhotosManager();

    public void approvePhoto(int orderId, String angle) throws SQLException {
        photosManager.approvePhoto(orderId, angle);
    }

    public void rejectPhoto(int orderId, String angle, String comment) throws SQLException {
        photosManager.rejectPhoto(orderId, angle, comment);
    }

    public Map<String, byte[]> getPhotosByOrderId(int orderId) throws SQLException {
        return photosManager.getPhotosByOrderId(orderId);
    }

    public Map<String, String> getPhotoStatusByOrderId(int orderId) throws SQLException {
        return photosManager.getPhotoStatusByOrderId(orderId);
    }


}
