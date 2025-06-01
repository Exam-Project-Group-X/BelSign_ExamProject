package easv.dk.belsign.gui.controllers.QAEmployee.report;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.QCReport;
import easv.dk.belsign.gui.models.QCReportModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;
import java.io.IOException;

public class QCReportPage1Controller implements Initializable {
    @FXML private Label lblQaSignDate;
    @FXML private Label lblOrderNo;
    @FXML private Label lblProductDes;
    @FXML private Label lblDate;
    @FXML private Label lblCreateTime1;
    @FXML private AnchorPane qaSignaturePane;   // the box for qa signature

    private Canvas sigCanvas;     // added at runtime
    private GraphicsContext gc;            // for drawing signature
    private boolean signed = false;
    private final QCReportModel model = new QCReportModel();
    private QCReport qcReport;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // create a canvas for signature
        sigCanvas = new Canvas();
        sigCanvas.setManaged(false);
        sigCanvas.widthProperty().bind(qaSignaturePane.widthProperty());
        sigCanvas.heightProperty().bind(qaSignaturePane.heightProperty());
        qaSignaturePane.getChildren().add(sigCanvas);

        // set up pen
        gc = sigCanvas.getGraphicsContext2D();
        gc.setLineWidth(2);
        // Timestamp footer
        lblCreateTime1.setText("This PDF was created at " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yy, HH:mm")));
    }

    public void setQcReport(QCReport r, String productDescription) {
        lblProductDes.setText(productDescription);
    }

    public void setOrderDetails(Order order) {
        lblOrderNo.setText(order.getOrderNumber());
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        lblProductDes.setText(order.getProductDescription());
    }

    /*  ── Mouse handlers wired in FXML ─────────────────────────────── */
    @FXML private void handleSigPress(MouseEvent e) {
        gc.beginPath();
        gc.moveTo(e.getX(), e.getY());
        gc.stroke();
        signed = true;
    }

    @FXML private void handleSigDrag(MouseEvent e) {
        gc.lineTo(e.getX(), e.getY());
        gc.stroke();
    }

    public boolean isSigned() { return signed; }

    /** PNG bytes you can save in DB or embed in PDF */
    public byte[] getSignaturePng() throws IOException {
        WritableImage img = sigCanvas.snapshot(null, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", os);
        return os.toByteArray();
    }

    public void clearSignature() {
        gc.clearRect(0, 0, sigCanvas.getWidth(), sigCanvas.getHeight());
        signed = false;
    }
}




