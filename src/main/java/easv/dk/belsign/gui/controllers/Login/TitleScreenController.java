package easv.dk.belsign.gui.controllers.Login;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.shape.Line;
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
        ViewManager.INSTANCE.showScene(FXMLPath.OPERATOR_DASHBOARD);
    }

    public void handleLineMousePressed(MouseEvent mouseEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
    }

    public void handleLineMouseReleased(MouseEvent mouseEvent) {

    }
}
