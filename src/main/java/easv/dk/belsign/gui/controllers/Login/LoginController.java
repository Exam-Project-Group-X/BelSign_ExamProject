package easv.dk.belsign.gui.controllers.Login;

import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

/*
🧠 ViewManager Reminder:

- To switch screens: ViewManager.INSTANCE.showScene(FXMLPath.XYZ);
- To open a popup:   ViewManager.INSTANCE.showStage(FXMLPath.XYZ, "Popup Title", true);
- Add new FXML paths inside: FXMLPath.java
- Never load FXML manually (no FXMLLoader.load(...)).
- All FXMLs must be inside: /src/main/resources/easv/dk/belsign/views/
*/

public class LoginController implements Initializable {
    @FXML
    private TextField loginEmail;
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField visiblePassword;
    @FXML
    private FontIcon eyeIcon;
    @FXML
    private Button btnTogglePassword;

    private boolean passwordVisible = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Hook up the login button
        loginButton.setOnAction(this::onLoginClick);
    }

    public void onTogglePasswordVisibility(ActionEvent actionEvent) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            // Show plain text field
            visiblePassword.setText(loginPassword.getText());
            visiblePassword.setVisible(true);
            visiblePassword.setManaged(true);
            loginPassword.setVisible(false);
            loginPassword.setManaged(false);
            // Change icon to indicate visibility
            eyeIcon.setIconLiteral("bi-eye-slash");
        } else {
            // Hide plain text field, show PasswordField again
            loginPassword.setText(visiblePassword.getText());
            loginPassword.setVisible(true);
            loginPassword.setManaged(true);
            visiblePassword.setVisible(false);
            visiblePassword.setManaged(false);
            // Change icon back to "eye"
            eyeIcon.setIconLiteral("bi-eye");
        }
    }

    public void onLoginClick(ActionEvent event) {
        // For now, just always open QA Orders view
        ViewManager.INSTANCE.showScene(FXMLPath.QA_EMPLOYEE_VIEW);
        System.out.println("Login button clicked!");
        String email = loginEmail.getText().trim();
        String password = loginPassword.getText();
    }


}
