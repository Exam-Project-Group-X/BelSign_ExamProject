package easv.dk.belsign.gui.controllers.Admin;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.AlertUtil;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditUserController implements Initializable {

    @FXML public TextField roleField;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button cancelBtn;
    @FXML private Button continueBtn;

    private static final UserModel userModel = new UserModel();
    private AdminController adminController;
    private User user;
    private User originalUser;
    private boolean fieldsChanged = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ObservableList<String> roles = userModel.getAllRoleNames();
            addFieldListeners();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addFieldListeners() {
        fullNameField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
    }

    private void checkIfChanged() {
        fieldsChanged =
                !fullNameField.getText().equals(originalUser.getFullName()) ||
                        !usernameField.getText().equals(originalUser.getUsername()) ||
                        !passwordField.getText().isBlank();

        continueBtn.setText(fieldsChanged ? "Update" : "Save");
        cancelBtn.setText(fieldsChanged ? "Revert" : "Close");
    }

    public void setManageUsersController(AdminController adminController) {
        this.adminController = adminController;
    }

    public void onClickLogoutBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.TITLE_SCREEN);
    }

    public void onClickCancelBtn(ActionEvent actionEvent) {
        if (fieldsChanged) {
            setUserData(originalUser);
            fieldsChanged = false;
        } else {
            navigateBack();
        }
    }

    public void onClickContinueBtn(ActionEvent actionEvent) {
        Window owner = ((Node) actionEvent.getSource()).getScene().getWindow();

        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        String roleName = roleField.getText();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || (user == null && rawPassword.isEmpty())) {
            AlertUtil.showErrorNotification(owner, "Validation Error", "Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            AlertUtil.showErrorNotification(owner, "Invalid Email", "Please enter a valid email address.");
            return;
        }

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

        String finalPassword = (user != null && user.getUserID() > 0)
                ? getFinalPassword(rawPassword, user.getPasswordHash())
                : PasswordUtils.hashPassword(rawPassword);

        try {
            if (user != null && user.getUserID() > 0) {
                User updatedUser = new User(
                        user.getUserID(),
                        username,
                        finalPassword,
                        "",
                        fullName,
                        email,
                        roleId,
                        null, null,
                        true,
                        roleName
                );
                userModel.updateUser(updatedUser);
                originalUser = updatedUser;
                fieldsChanged = false;
                AlertUtil.showSuccessNotification(owner, "Success", "User updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showErrorNotification(owner, "Error", "Failed to update user.");
            return;
        }

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

    public void setUserData(User user) {
        this.user = user;
        this.originalUser = user;
        fullNameField.setText(user.getFullName());
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        passwordField.setText("");
        roleField.setText(user.getRoleName());

        emailField.setDisable(true);
        roleField.setDisable(true);

        continueBtn.setText("Save");
        cancelBtn.setText("Close");
    }

    private String getFinalPassword(String newPasswordInput, String oldPasswordHash) {
        return (newPasswordInput == null || newPasswordInput.trim().isEmpty())
                ? oldPasswordHash
                : PasswordUtils.hashPassword(newPasswordInput.trim());
    }
}

