package easv.dk.belsign.utils;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.util.Duration;
public final class AlertUtil {

    private AlertUtil() {

    }
    public static void success(Scene scene, String msg) {
        show(scene, msg, "#0FA958");
    }

    public static void error(Scene scene, String msg) {
        show(scene, msg, "#D92C2C");
    }

    public static void info(Scene scene, String msg) {
        show(scene, msg, "#007ACD");
    }

    private static void show(Scene scene, String msg, String bg) {

        Label toast = new Label(msg);
        toast.setStyle(
                "-fx-background-color:" + bg + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:16px;" +
                        "-fx-font-weight:600;" +
                        "-fx-background-radius:3px;"
        );
        toast.setPadding(new Insets(10, 20, 10, 20));
        toast.setOpacity(0);

        Parent root = scene.getRoot();
        StackPane overlay;
        if (root instanceof StackPane sp) {
            overlay = sp;
        } else {
            overlay = new StackPane();
            overlay.setPickOnBounds(false);
            overlay.getChildren().add(root);
            scene.setRoot(overlay);
        }
        overlay.getChildren().add(toast);
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 2, 0));

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), toast);
        slideIn.setFromY(50);
        slideIn.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), toast);
        slideOut.setFromY(0);
        slideOut.setToY(50);
        slideOut.setOnFinished(e -> overlay.getChildren().remove(toast));

        ParallelTransition parallelIn = new ParallelTransition(slideIn, fadeIn);
        ParallelTransition parallelOut = new ParallelTransition(slideOut, fadeOut);

        new SequentialTransition(parallelIn, stay, parallelOut).play();
    }
}
