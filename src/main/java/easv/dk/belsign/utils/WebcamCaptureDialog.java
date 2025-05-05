package easv.dk.belsign.utils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WebcamCaptureDialog {

    private Webcam webcam;
    private BufferedImage capturedImage;

    public Image showAndCapture() {
        System.out.println("arch = " + System.getProperty("os.arch"));
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);

        JButton captureButton = new JButton("Capture");
        captureButton.addActionListener(e -> {
            capturedImage = webcam.getImage();
            synchronized (this) {
                this.notify();  // Notify that capture happened
            }
        });

        JFrame window = new JFrame("Capture Photo");
        window.setLayout(new BorderLayout());
        window.add(panel, BorderLayout.CENTER);
        window.add(captureButton, BorderLayout.SOUTH);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setVisible(true);

        synchronized (this) {
            try {
                this.wait(); // Wait for capture
            } catch (InterruptedException ignored) {}
        }

        webcam.close();
        window.dispose();

        return capturedImage != null ? SwingFXUtils.toFXImage(capturedImage, null) : null;
    }
}
