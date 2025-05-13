package easv.dk.belsign.gui.controllers.Login;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.models.UserModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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

    private final UserModel userModel = new UserModel();

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
        String email = loginEmail.getText().trim();
        String password = passwordVisible ? visiblePassword.getText() : loginPassword.getText();

        // Validate email format
        if (email.isEmpty() || !isValidEmail(email)) {
            ViewManager.INSTANCE.showError("Login failed", "Invalid email format");
            return;
        }

        // Check if password is empty
        if (password.isEmpty()) {
            ViewManager.INSTANCE.showError("Login failed", "Password cannot be empty");
            return;
        }

        User user = userModel.authenticate(email, password);
        if (user != null) {
            switch (user.getRoleName()) {
                case "Admin" -> ViewManager.INSTANCE.showScene(FXMLPath.ADMIN_DASHBOARD);
                case "QA Employee" -> ViewManager.INSTANCE.showScene(FXMLPath.QA_EMPLOYEE_VIEW);
                default -> ViewManager.INSTANCE.showError("Login failed", "Access denied for role: " + user.getRoleName());
            }
        } else {
            ViewManager.INSTANCE.showError("Login failed", "Invalid email or password");
        }
    }

    // Helper method to validate email format using a regular expression
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public void onClickLogoutBtn(ActionEvent actionEvent) {

        ViewManager.INSTANCE.showScene(FXMLPath.TITLE_SCREEN);
    }
}
