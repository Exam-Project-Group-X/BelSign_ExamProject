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
import easv.dk.belsign.utils.AlertUtil;
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
import javafx.scene.layout.Region;
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


public class QCReportMainController {
    @FXML private QCReportPage1Controller page1Controller;
    @FXML private VBox vboxPages;

    private Order selectedOrder;
    private final ProductPhotosDAO photoDAO = new ProductPhotosDAO();

    private static final int TARGET_DPI = 300;
    private static final double MARGIN_PT = 36;

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

    public void onClickPrintBtn(ActionEvent e) {

        /* 1 ─ file chooser */
        FileChooser fc = new FileChooser();
        fc.setTitle("Save QC report");
        fc.setInitialFileName("QC_Report_" + selectedOrder.getOrderID() + ".pdf");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File outFile = fc.showSaveDialog(vboxPages.getScene().getWindow());
        if (outFile == null) return;

        /* 2 ─ compute uniform scale factor once */
        double screenDpi = javafx.stage.Screen.getPrimary().getDpi();   // ≈ 96
        double scale     = TARGET_DPI / screenDpi;                      // ≈ 3.125

        Platform.runLater(() -> {
            try (PdfWriter wr   = new PdfWriter(outFile);
                 PdfDocument pdf = new PdfDocument(wr);
                 Document doc   = new Document(pdf, PageSize.A4)) {

                final float maxW = (float) (PageSize.A4.getWidth()  - 2*MARGIN_PT);
                final float maxH = (float) (PageSize.A4.getHeight() - 2*MARGIN_PT);

                SnapshotParameters snap = new SnapshotParameters();
                snap.setTransform(new javafx.scene.transform.Scale(scale, scale));

                for (int i = 0, n = vboxPages.getChildren().size(); i < n; i++) {

                    Node page = vboxPages.getChildren().get(i);
                    page.applyCss();
                    if (page instanceof javafx.scene.layout.Region r) r.layout();

                    WritableImage fxImg = page.snapshot(snap, null);
                    BufferedImage awt  = SwingFXUtils.fromFXImage(fxImg, null);

                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ImageIO.write(awt, "png", baos);
                        Image pdfImg = new Image(ImageDataFactory.create(baos.toByteArray()));
                        pdfImg.scaleToFit(maxW, maxH);           // keeps aspect ratio
                        doc.add(pdfImg);
                    }
                    if (i < n - 1) doc.add(new AreaBreak());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertUtil.error(((Node) e.getSource()).getScene(),
                        "Error – failed to create PDF.");
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