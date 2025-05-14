package easv.dk.belsign.utils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
public class AlertUtil {
    private static Runnable onConfirm;
    private AlertUtil() {
        // Private constructor to prevent instantiation
    }
    public static void showSuccessNotification(Window owner, String title, String message) {
        Label content = createNotificationContent(title, message, "#35B587");
        Notifications.create()
                .owner(owner)
                .graphic(content)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_CENTER)
                // Clear default title and text since we are using our custom graphic.
                .title("")
                .text("")
                .show();
    }
    public static void showErrorNotification(Window owner, String title, String message) {
        Label content = createNotificationContent(title, message, "#E57373");
        Notifications.create()
                .owner(owner)
                .graphic(content)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_CENTER)
                .title("")
                .text("")
                .show();
    }
    public static void showWarningNotification(Window owner, String title, String message) {
        Label content = createNotificationContent(title, message, "#F4B400");
        Notifications.create()
                .owner(owner)
                .graphic(content)
                .hideAfter(Duration.seconds(5)) // Allow a bit more time for deletion confirmation
                .position(Pos.BOTTOM_CENTER)
                .title("")
                .text("")
                .show();
    }
    private static Label createNotificationContent(String title, String message, String textColor) {
        Label label = new Label(title + ": " + message);
        // Set a transparent background, desired text color and font styling.
        label.setStyle(
                "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 15px 20px;" +    // Increase vertical padding for a higher label
                        "-fx-background-radius: 3px;"   // Rounded corners with radius 5px
        );
        return label;
    }
}
