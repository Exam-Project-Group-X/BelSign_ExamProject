package easv.dk.belsign.gui.models;

import easv.dk.belsign.bll.ProductPhotosManager;

import java.sql.SQLException;

public class PhotosModel {

    ProductPhotosManager photosManager = new ProductPhotosManager();

    public void approvePhoto(int orderId, String angle) throws SQLException {
        photosManager.approvePhoto(orderId, angle);
    }

    public void rejectPhoto(int orderId, String angle) throws SQLException {
        photosManager.rejectPhoto(orderId, angle);
    }

}
