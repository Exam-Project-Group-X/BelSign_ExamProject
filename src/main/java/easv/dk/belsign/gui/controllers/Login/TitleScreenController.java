package easv.dk.belsign.gui.controllers.Login;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.Dimension;
import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.web.UserDAO;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class TitleScreenController {

    @FXML
    private Rectangle swipeRect;
    private final UserDAO userDAO = new UserDAO();

    public void initialize() {
        animateRectangle();
    }

    private void animateRectangle() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), swipeRect);
        transition.setFromY(0);
        transition.setToY(-20);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
    }

    @FXML
    public void onClickScanBtn(ActionEvent actionEvent) {
        System.out.println("Opening camera to scan QR code...");
        String scannedCode = scanQRCode();
        if (scannedCode == null) {
            System.out.println("QR code not detected");
            return;
        }

        System.out.println("Scanned Access Code: " + scannedCode);
        User user = userDAO.getUserByAccessCode(scannedCode);

        if (user != null) {
            System.out.println("Access granted to user: " + user.getFullName());
            ViewManager.INSTANCE.showScene(FXMLPath.OPERATOR_DASHBOARD);
        } else {
            System.out.println("Invalid Access Code. No matching user found.");
        }
    }

    private String scanQRCode() {
        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            System.out.println("No webcam detected.");
            return null;
        }

        webcam.setViewSize(new Dimension(320, 240));
        webcam.open();

        // Swing UI for camera preview
        JFrame window = new JFrame("Scan QR Code");
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setMirrored(true);
        window.add(panel);
        window.pack();
        window.setVisible(true);

        String resultText = null;
        long timeout = System.currentTimeMillis() + 20000; // 20 seconds

        while (System.currentTimeMillis() < timeout && resultText == null) {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    resultText = result.getText();
                } catch (NotFoundException e) {
                    // No QR code yet, keep scanning
                }
            }

            try {
                Thread.sleep(200); // avoid 100% CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        webcam.close();
        window.dispose();

        return resultText;
    }


    public void swipeToLogin(SwipeEvent swipeEvent) {}

    public void handleLineMousePressed(MouseEvent mouseEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
        System.out.println("Swipe detected! Navigating to QA Employee View.");
    }

    public void handleLineMouseReleased(MouseEvent mouseEvent) {}
}
