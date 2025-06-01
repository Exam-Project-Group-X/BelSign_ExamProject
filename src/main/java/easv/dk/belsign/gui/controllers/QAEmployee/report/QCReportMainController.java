package easv.dk.belsign.gui.controllers.QAEmployee.report;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.models.PhotosModel;
import easv.dk.belsign.gui.util.AlertUtil;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Consumer;

public class QCReportMainController {
    @FXML private VBox vboxPages;

    private Consumer<File> reportSaveListener;
    private Order order;
    private final PhotosModel photosModel = new PhotosModel();

    public void setReportSaveListener(Consumer<File> l) { this.reportSaveListener = l; }

    /* ---------- pagination constants ---------- */
    private static final int    TARGET_DPI = 300;
    private static final double MARGIN_PT  = 36;
    private static final Path   REPORTS_BASE = projectRoot().resolve("reports");

    public void setSelectedOrder(Order o) {
        this.order = o;
        vboxPages.getChildren().clear();
        try {
            loadPage1();
            Map<String, byte[]> photosByAngle = photosModel.getPhotosByOrderId(o.getOrderID());
            loadPhotoPages(photosByAngle);
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
            AlertUtil.error(vboxPages.getScene(), "❌ Failed to load report.");
        }
    }

    public void onClickPrintBtn(ActionEvent evt) {
        /* ask where store the pdf */
        FileChooser fc = new FileChooser();
        fc.setTitle("Save QC report");
        fc.setInitialFileName("QC_Report_" + order.getOrderID() + ".pdf");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF files (*.pdf)","*.pdf"));
        File userCopy = fc.showSaveDialog(vboxPages.getScene().getWindow());
        if (userCopy == null) return;
        /* render & save on FX thread (needs snapshots) */
        Platform.runLater(() -> {
            try {
                Path tempPdf   = Files.createTempFile("qc_", ".pdf");
                renderPdf(tempPdf);
                Path sharedPdf = copyToSharedFolder(tempPdf);
                Files.copy(sharedPdf, userCopy.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                if (reportSaveListener != null)
                    reportSaveListener.accept(sharedPdf.toFile());
                /* close this preview window */
                Stage stage = (Stage) vboxPages.getScene().getWindow();
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                AlertUtil.error(((Node) evt.getSource()).getScene(),
                        "❌ Could not create or save the PDF.");
            }
        });
    }

    /* ======================  load pages on main controller  =========================== */
    private void loadPage1() throws IOException {
        FXMLLoader fxml = new FXMLLoader(getClass()
                .getResource("/easv/dk/belsign/views/QAViews/QCReport/QCReportPage1.fxml"));
        AnchorPane page1 = fxml.load();
        fxml.<QCReportPage1Controller>getController().setOrderDetails(order);
        vboxPages.getChildren().add(page1);
    }

    private void loadPhotoPages(Map<String, byte[]> photos) throws IOException {
        final int perPage = 6;
        List<Map.Entry<String,byte[]>> list = new ArrayList<>(photos.entrySet());
        for (int i = 0; i < list.size(); i += perPage) {
            FXMLLoader fxml = new FXMLLoader(getClass()
                    .getResource("/easv/dk/belsign/views/QAViews/QCReport/QCReportPhotos.fxml"));
            AnchorPane page = fxml.load();
            PhotoPageController c = fxml.getController();
            c.setPhotosFromBytes(list.subList(i, Math.min(i+perPage, list.size())));
            c.setPageNumber(1 + vboxPages.getChildren().size());        // page #2…
            vboxPages.getChildren().add(page);
        }
    }

    private void renderPdf(Path out) throws Exception {
        double scale = TARGET_DPI / javafx.stage.Screen.getPrimary().getDpi();
        SnapshotParameters snap = new SnapshotParameters();
        snap.setTransform(new Scale(scale, scale));
        try (PdfWriter wr = new PdfWriter(out.toFile());
             PdfDocument pdf = new PdfDocument(wr);
             Document doc = new Document(pdf, PageSize.A4)) {

            float maxW = (float)(PageSize.A4.getWidth()  - 2*MARGIN_PT);
            float maxH = (float)(PageSize.A4.getHeight() - 2*MARGIN_PT);

            for (int i = 0, n = vboxPages.getChildren().size(); i < n; i++) {
                Node page = vboxPages.getChildren().get(i);
                page.applyCss();  if (page instanceof Region r) r.layout();

                WritableImage wi = page.snapshot(snap, null);
                BufferedImage bi = SwingFXUtils.fromFXImage(wi, null);
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(bi, "png", baos);
                    Image img = new Image(ImageDataFactory.create(baos.toByteArray()));
                    img.scaleToFit(maxW,maxH);
                    doc.add(img);
                }
                if (i < n-1) doc.add(new AreaBreak());
            }
        }
    }

    private Path copyToSharedFolder(Path tempPdf) throws IOException {
        YearMonth ym = YearMonth.now();
        Path dir = REPORTS_BASE.resolve(String.valueOf(ym.getYear()))
                .resolve("%02d".formatted(ym.getMonthValue()));
        Files.createDirectories(dir);
        Path target = dir.resolve(tempPdf.getFileName());
        return Files.move(tempPdf, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private static Path projectRoot() {
        try {
            Path classesDir = Paths.get(
                    QCReportMainController.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());                // use URI, not plain String
            return classesDir                     // …/target/classes
                    .getParent()                  // …/target
                    .getParent();                 // …/BelSign_ExamProject
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot resolve project root", e);
        }
    }
}
