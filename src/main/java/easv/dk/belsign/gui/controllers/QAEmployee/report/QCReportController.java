package easv.dk.belsign.gui.controllers.QAEmployee.report;
import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.web.ProductPhotosDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class QCReportController {

    private Order selectedOrder;
    private final ProductPhotosDAO photoDAO = new ProductPhotosDAO();

    @FXML private QCReportPage1Controller page1Controller;

    @FXML
    private VBox vboxPages;


    public void setSelectedOrder(Order order) {
        this.selectedOrder = order;

        try {
            // ✅ Manually load Page 1
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/belsign/views/QAViews/QCReport/QCReportPage1.fxml"));
            AnchorPane page1 = loader.load();
            QCReportPage1Controller page1Controller = loader.getController();

            page1Controller.setOrderDetails(order);
            vboxPages.getChildren().add(page1); // Add Page 1 as first child

            // ✅ Load additional photo pages
            Map<String, byte[]> photoMap = photoDAO.getPhotosByOrderId(order.getOrderID());
            loadPhotoPages(photoMap);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load report pages.").showAndWait();
        }
    }


    public void onClickPrintBtn(ActionEvent actionEvent) {
        // You can export PDF here if needed
    }

    private void loadPhotoPages(Map<String, byte[]> photoMap) throws IOException {
        int photosPerPage = 6;
        List<Map.Entry<String, byte[]>> entries = new ArrayList<>(photoMap.entrySet());
        int totalPages = (int) Math.ceil(entries.size() / (double) photosPerPage);

        for (int i = 0; i < totalPages; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/dk/belsign/views/QAViews/QCReport/QCReportPhotos.fxml"));
            AnchorPane photoPage = loader.load();

            PhotoPageController controller = loader.getController();

            List<Map.Entry<String, byte[]>> batch = entries.subList(
                    i * photosPerPage,
                    Math.min((i + 1) * photosPerPage, entries.size())
            );

            controller.setPhotosFromBytes(batch);
            controller.setPageNumber(i + 2); // Page 1 is fixed

            vboxPages.getChildren().add(photoPage);
        }
    }

}