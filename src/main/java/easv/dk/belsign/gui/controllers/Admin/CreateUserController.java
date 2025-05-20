package easv.dk.belsign.gui.controllers.Admin;


import easv.dk.belsign.be.User;
import easv.dk.belsign.utils.AlertUtil;
import javafx.stage.Window;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.models.UserModel;
import easv.dk.belsign.utils.PasswordUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateUserController implements Initializable {

    @FXML private Label labelRole;
    @FXML private Button cancelBtn;
    @FXML private Label actionLabel;
    @FXML private Label fullNameLabel, emailLabel, passwordLabel;
    @FXML private Button continueBtn;
    @FXML private TextField fullNameField, emailField, passwordField;
    @FXML private ComboBox<String> roleComboBox;

    private static final UserModel userModel = new UserModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ObservableList<String> roles = userModel.getAllRoleNames();
            roleComboBox.setItems(roles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onClickLogoutBtn(ActionEvent event) {
        ViewManager.INSTANCE.showScene(FXMLPath.TITLE_SCREEN);
    }

    public void onClickContinueBtn(ActionEvent event) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        Object selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (fullName.isEmpty() || email.isEmpty() || rawPassword.isEmpty()) {
            AlertUtil.showErrorNotification(owner, "Validation Error", "Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            AlertUtil.showErrorNotification(owner, "Invalid Email", "Please enter a valid email address.");
            return;
        }

        if (selectedRole == null) {
            AlertUtil.showErrorNotification(owner, "Role Missing", "Please select a role.");
            return;
        }

        String roleName = selectedRole.toString();
        int roleId = switch (roleName) {
            case "Admin" -> 1;
            case "QA Employee" -> 2;
            case "Operator" -> 3;
            default -> {
                AlertUtil.showErrorNotification(owner, "Unknown Role", "Invalid role selected.");
                yield -1;
            }
        };
        if (roleId == -1) return;

        String hashedPassword = PasswordUtils.hashPassword(rawPassword);

        try {
            for (User existingUser : userModel.getAllUsers()) {
                if (existingUser.getEmail().equalsIgnoreCase(email)) {
                    AlertUtil.showErrorNotification(owner, "Duplicate Email", "A user with this email already exists.");
                    return;
                }
            }

            User newUser = new User(0, hashedPassword, "", fullName, email, roleId, null, null, true, roleName);
            userModel.createNewUser(newUser);

            AlertUtil.showSuccessNotification(owner, "Success", "User created.");
            navigateBack();


        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showErrorNotification(owner, "Database Error", "Failed to save user.");
        }
    }

    public void onClickCancelBtn(ActionEvent event) {
        navigateBack();
    }

    private void navigateBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.ADMIN_DASHBOARD));
            Parent root = loader.load();
            Stage currentStage = (Stage) cancelBtn.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
