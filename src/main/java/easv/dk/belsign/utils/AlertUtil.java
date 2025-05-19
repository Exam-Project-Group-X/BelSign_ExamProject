package easv.dk.belsign.utils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
public class AlertUtil {

    private AlertUtil() {
        // Private constructor to prevent instantiation
    }
    /**
     * Displays a success notification with a custom graphic.
     *
     * @param owner   The owner window of the notification.
     * @param title   The title of the notification.
     * @param message The message of the notification.
     */
    public static void showSuccessNotification(Window owner, String title, String message) {
        Label content = createNotificationContent(title, message, "#0FA958");
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
    /**
     * Displays an warning notification with a custom graphic.
     *
     * @param owner   The owner window of the notification.
     * @param title   The title of the notification.
     * @param message The message of the notification.
     */
    public static void showErrorNotification(Window owner, String title, String message) {
        Label content = createNotificationContent(title, message, "#D92C2C");
        Notifications.create()
                .owner(owner)
                .graphic(content)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_CENTER)
                .title("")
                .text("")
                .show();
    }

    private static Label createNotificationContent(String title, String message, String bgColor) {
        Label label = new Label(title + ": " + message);
        // Set a transparent background, desired text color and font styling.
        label.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 15px 20px;" +
                        "-fx-background-radius: 3px;" +
                        "-fx-background-color: " + bgColor + ";"
        );
        return label;
    }
}
