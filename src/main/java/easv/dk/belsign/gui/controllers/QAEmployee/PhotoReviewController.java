package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.event.ActionEvent;

public class PhotoReviewController {
    public void onClickLogoutBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
    }
}
