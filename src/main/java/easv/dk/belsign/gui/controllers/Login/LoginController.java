package easv.dk.belsign.gui.controllers.Login;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.*;

import easv.dk.belsign.gui.controllers.Admin.AdminController;
import easv.dk.belsign.gui.controllers.QAEmployee.QAEmployeeController;
import easv.dk.belsign.gui.models.UserModel;
import easv.dk.belsign.gui.util.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {
    @FXML
    private ImageView loginImage;
    @FXML
    private Button backButton;
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
            // Change icon back to "eye"
            eyeIcon.setIconLiteral("bi-eye");
        } else {
            // Hide plain text field, show PasswordField again
            loginPassword.setText(visiblePassword.getText());
            loginPassword.setVisible(true);
            loginPassword.setManaged(true);
            visiblePassword.setVisible(false);
            visiblePassword.setManaged(false);
            // Change icon to indicate visibility
            eyeIcon.setIconLiteral("bi-eye-slash");
        }
    }

    public void onLoginClick(ActionEvent event) {
        String email = loginEmail.getText().trim();
        String password = passwordVisible ? visiblePassword.getText() : loginPassword.getText();
        // Validate email format
        if (email.isEmpty() || !isValidEmail(email)) {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Error, Invalid email format.");
            return;
        }
        // Check if password is empty
        if (password.isEmpty()) {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Error, Password cannot be empty!");
            return;
        }
        User user = null;
        try {
            user = userModel.authenticate(email, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (user != null) {
            switch (user.getRoleName()) {
                case "Admin" -> {
                    Pair<Parent, AdminController> adminView = FXMLManager.INSTANCE.getFXML(FXMLPath.ADMIN_DASHBOARD);
                    adminView.getValue().setLoggedInUser(user);
                    Navigation.goToAdminView(adminView.getKey());
                }
                case "QA Employee" -> {
                    Pair<Parent, QAEmployeeController> qaView = FXMLManager.INSTANCE.getFXML(FXMLPath.QA_EMPLOYEE_VIEW);
                    qaView.getValue().setLoggedInUser(user);
                   Navigation.goToQAEmployeeView(qaView.getKey());
                }
                default -> {
                    AlertUtil.error(
                            ((Node) event.getSource()).getScene(),
                            "Login failed, Access denied for role: " + user.getRoleName());

                }
            }
        } else {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Error, Invalid email or password");
        }
    }

    // Helper method to validate e-mail format using a regular expression
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public void onBackClick(ActionEvent actionEvent) { Navigation.goToTitleScreen();
    }
}
