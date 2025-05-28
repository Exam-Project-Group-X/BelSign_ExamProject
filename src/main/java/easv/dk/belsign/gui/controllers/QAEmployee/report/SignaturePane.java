package easv.dk.belsign.gui.controllers.QAEmployee.report;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignaturePane extends Canvas {
    private final GraphicsContext gc = getGraphicsContext2D();
    private boolean signed = false;

    public SignaturePane() {
        setWidth(260);
        setHeight(70);
        gc.setLineWidth(2);

        /* Basic white background + border (optional) */
        setStyle("-fx-background-color:white; -fx-border-color:#003F73;");

        /* Draw on drag */
        setOnMousePressed(e -> {
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
        });
        setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
            signed = true;
        });
    }

    /** True once at least one stroke was drawn. */
    public boolean isSigned() { return signed; }

    /** Clear the canvas. */
    public void clear() {
        gc.clearRect(0, 0, getWidth(), getHeight());
        signed = false;
    }

    /** Return the signature as PNG bytes (≈ 5–8 KB). */
    public byte[] toPng() throws IOException {
        WritableImage img = new WritableImage((int) getWidth(), (int) getHeight());
        snapshot(null, img);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", os);
            return os.toByteArray();
        }
    }
}
