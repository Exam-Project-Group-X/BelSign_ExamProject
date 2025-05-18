package easv.dk.belsign.gui.controllers.Login;

import easv.dk.belsign.gui.ViewManagement.Navigation;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class TitleScreenController {
    @FXML
    private Rectangle swipeRect;

    public void initialize() {
        animateRectangle();
    }

    // Animates the rectangle moving up and down continuously.
    private void animateRectangle() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1.5), swipeRect);
        transition.setFromY(0);
        transition.setToY(-3);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
    }

    public void onClickScanBtn(ActionEvent actionEvent) {
        Navigation.goToOperatorDashboard();
    }

    public void handleLineMousePressed(MouseEvent mouseEvent) {
        Navigation.goToLoginScreen();
    }

    public void handleLineMouseReleased(MouseEvent mouseEvent) {

    }
}
