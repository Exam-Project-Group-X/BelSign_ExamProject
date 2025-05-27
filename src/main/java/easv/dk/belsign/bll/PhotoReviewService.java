package easv.dk.belsign.bll;

import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.models.QAEmployeeModel;

import java.sql.SQLException;
import java.util.Map;

public class PhotoReviewService {

    private final PhotosModel photosModel;
    private final QAEmployeeModel qaModel;

    public PhotoReviewService(PhotosModel photosModel, QAEmployeeModel qaModel) {
        this.photosModel = photosModel;
        this.qaModel = qaModel;
    }

    public boolean allPhotosApproved(int orderId) throws SQLException {
        Map<String, String> statusMap = photosModel.getPhotoStatusByOrderId(orderId);
        return statusMap.values().stream()
                .allMatch(status -> status.equalsIgnoreCase("Approved"));
    }

    public void completeOrder(int orderId) throws Exception {
        qaModel.setOrderToCompleted(orderId);
    }

    public void approvePhoto(int orderId, String angle) throws SQLException {
        photosModel.approvePhoto(orderId, angle);
    }

    public void rejectPhoto(int orderId, String angle, String comment) throws SQLException {
        photosModel.rejectPhoto(orderId, angle, comment);
    }

    public Map<String, byte[]> getPhotos(int orderId) throws SQLException {
        return photosModel.getPhotosByOrderId(orderId);
    }

    public Map<String, String> getStatuses(int orderId) throws SQLException {
        return photosModel.getPhotoStatusByOrderId(orderId);
    }
}
