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

    public Label labelRole;
    @FXML private Button cancelBtn;
    @FXML private Label actionLabel;
    @FXML private Label fullNameLabel, emailLabel, usernameLabel, passwordLabel;
    @FXML private Button continueBtn;
    @FXML private TextField usernameField, fullNameField, emailField, passwordField;
    @FXML private ComboBox<String> roleComboBox;

    private static final UserModel userModel = new UserModel();
    private boolean isEditMode = false;
    private boolean fieldsChanged = false;
    private User currentUserAfterCreate;

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

    private void addEditListeners() {
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
        fullNameField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
    }

    private void checkIfChanged() {
        fieldsChanged =
                !usernameField.getText().equals(currentUserAfterCreate.getUsername()) ||
                        !fullNameField.getText().equals(currentUserAfterCreate.getFullName()) ||
                        !passwordField.getText().isBlank();

        continueBtn.setText(fieldsChanged ? "Update" : "Save");
        cancelBtn.setText(fieldsChanged ? "Revert" : "Close");
    }

    public void onClickContinueBtn(ActionEvent event) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();

        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        Object selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || (!isEditMode && rawPassword.isEmpty())) {
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

        String finalPassword = isEditMode
                ? getFinalPassword(rawPassword, currentUserAfterCreate.getPasswordHash())
                : PasswordUtils.hashPassword(rawPassword);

        try {
            if (isEditMode) {
                User updatedUser = new User(currentUserAfterCreate.getUserID(), username, finalPassword, "", fullName, email, roleId, null, null, true, roleName);
                userModel.updateUser(updatedUser);
                currentUserAfterCreate = updatedUser;
                AlertUtil.showSuccessNotification(owner, "Success", "User updated.");
                navigateBack();
            } else {
                User newUser = new User(0, username, finalPassword, "", fullName, email, roleId, null, null, true, roleName);
                userModel.createNewUser(newUser);
                currentUserAfterCreate = newUser;
                AlertUtil.showSuccessNotification(owner, "Success", "User created.");
                enterEditMode();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.showErrorNotification(owner, "Database Error", "Failed to save user.");
        }
    }

    public void onClickCancelBtn(ActionEvent event) {
        if (!isEditMode) {
            navigateBack();
        } else if (fieldsChanged) {
            onClickRevert();
        } else {
            navigateBack();
        }
    }

    private void enterEditMode() {
        isEditMode = true;
        actionLabel.setText("Edit User Details");
        continueBtn.setText("Save");
        cancelBtn.setText("Close");

        roleComboBox.setDisable(true);
        emailField.setDisable(true);

        fullNameLabel.setText("FULL NAME:");
        emailLabel.setText("EMAIL:");
        usernameLabel.setText("USERNAME:");
        labelRole.setText("ROLE:");
        passwordLabel.setText("PASSWORD (optional):");

        addEditListeners();
    }

    private void onClickRevert() {
        usernameField.setText(currentUserAfterCreate.getUsername());
        fullNameField.setText(currentUserAfterCreate.getFullName());
        passwordField.setText("");
        fieldsChanged = false;

        continueBtn.setText("Save");
        cancelBtn.setText("Close");
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

    private String getFinalPassword(String newPasswordInput, String oldPasswordHash) {
        return (newPasswordInput == null || newPasswordInput.trim().isEmpty())
                ? oldPasswordHash
                : PasswordUtils.hashPassword(newPasswordInput.trim());
    }
}
