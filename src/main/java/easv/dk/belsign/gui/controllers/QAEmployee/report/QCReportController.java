package easv.dk.belsign.gui.controllers.QAEmployee.report;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import easv.dk.belsign.be.Order;
import easv.dk.belsign.dal.web.ProductPhotosDAO;
import easv.dk.belsign.gui.util.AlertUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
        /* ---------- Ask the user where to save pdf file---------- */
        FileChooser fc = new FileChooser();
        fc.setTitle("Save QC report");
        fc.setInitialFileName("QC_Report_" + selectedOrder.getOrderID() + ".pdf");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File outFile = fc.showSaveDialog(vboxPages.getScene().getWindow());
        if (outFile == null) return;            // user pressed Cancel
        Platform.runLater(() -> {
            try {
                PdfWriter writer = new PdfWriter(outFile.getAbsolutePath());
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc, PageSize.A4);

                int totalPages = vboxPages.getChildren().size();
                for (int i = 0; i < totalPages; i++) {
                    Node page = vboxPages.getChildren().get(i);
                    page.applyCss();
                    if (page instanceof javafx.scene.layout.Region) {
                        ((javafx.scene.layout.Region) page).layout();
                    }
                    // Create SnapshotParameters with a scale factor of 2
                    SnapshotParameters snapshotParams = new SnapshotParameters();
                    snapshotParams.setTransform(new Scale(2, 2));
                    WritableImage snapshot = page.snapshot(new SnapshotParameters(), null);
                    if (snapshot == null) {
                        System.err.println("Snapshot is null for page " + i);
                        continue;
                    }
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos);
                    baos.flush();
                    byte[] imageBytes = baos.toByteArray();
                    baos.close();

                    ImageData imageData = ImageDataFactory.create(imageBytes);
                    Image pdfImage = new Image(imageData);
                    pdfImage.scaleToFit(PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
                    document.add(pdfImage);

                    if (i < totalPages - 1) {
                        document.add(new AreaBreak());
                    }
                }
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.error(((Node) actionEvent.getSource()).getScene(), "Error! Failed to create PDF.");
            }
        });

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