package easv.dk.belsign.gui.LoginControllers;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.SwipeEvent;

public class MainLoginController {
    public void onClickScanBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.OPERATOR_DASHBOARD);
        System.out.println("scane btn detected! Navigating to operator View.");
    }

    public void swipeToLogin(SwipeEvent swipeEvent) {

    }

    public void handleLineMousePressed(MouseEvent mouseEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
        System.out.println("Swipe detected! Navigating to QA Employee View.");
    }

    public void handleLineMouseReleased(MouseEvent mouseEvent) {

    }
}
