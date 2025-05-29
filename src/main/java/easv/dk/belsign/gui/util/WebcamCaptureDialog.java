package easv.dk.belsign.gui.util;

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

    // Custom circular button class
    static class CircularButton extends JButton {
        public CircularButton(String label) {
            super(label);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setPreferredSize(new Dimension(80, 80)); // Big enough circle button
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw circle background
            if (getModel().isPressed()) {
                g2.setColor(Color.DARK_GRAY);
            } else if (getModel().isRollover()) {
                g2.setColor(Color.LIGHT_GRAY);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.fillOval(0, 0, getWidth(), getHeight());

            // Draw circle border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

            // Draw inner circle to simulate shutter button style
            g2.setColor(Color.WHITE);
            int inset = 15;
            g2.fillOval(inset, inset, getWidth() - 2 * inset, getHeight() - 2 * inset);

            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            // Only clicks inside the circle count
            int radius = getWidth() / 2;
            int centerX = radius;
            int centerY = radius;
            return ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) <= radius * radius;
        }
    }

    public Image showAndCapture() {
        System.out.println("arch = " + System.getProperty("os.arch"));
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);

        CircularButton captureButton = new CircularButton("");
        captureButton.addActionListener(e -> {
            capturedImage = webcam.getImage();
            synchronized (this) {
                this.notify();  // Notify that capture happened
            }
        });

        JFrame window = new JFrame("Capture Photo");
        window.setLayout(new BorderLayout());
        window.add(panel, BorderLayout.CENTER);

        // Panel to hold the button and center it horizontally
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20)); // center, 20 px vertical gap
        buttonPanel.setBackground(Color.DARK_GRAY); // optional: dark background for contrast
        buttonPanel.add(captureButton);
        window.add(buttonPanel, BorderLayout.SOUTH);

        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setResizable(false);

        window.setSize(1000, 768);
        window.setLocationRelativeTo(null); // center on screen

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
