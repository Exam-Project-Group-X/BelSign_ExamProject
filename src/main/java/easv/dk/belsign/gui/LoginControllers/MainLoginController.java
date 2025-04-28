package easv.dk.belsign.gui.LoginControllers;

import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

/*
🧠 ViewManager Reminder:

- To switch screens: ViewManager.INSTANCE.showScene(FXMLPath.XYZ);
- To open a popup:   ViewManager.INSTANCE.showStage(FXMLPath.XYZ, "Popup Title", true);
- Add new FXML paths inside: FXMLPath.java
- Never load FXML manually (no FXMLLoader.load(...)).
- All FXMLs must be inside: /src/main/resources/easv/dk/belsign/views/
*/

public class MainLoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotLink;

    @FXML
    private void initialize() {
        // Hook up the login button
        loginButton.setOnAction(this::onLoginClick);
    }

    private void onLoginClick(ActionEvent event) {
        // For now, just always open QA Orders view
        ViewManager.INSTANCE.showScene(FXMLPath.QA_ORDERS);
        System.out.println("Login button clicked!");
    }

}
