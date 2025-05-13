package easv.dk.belsign.gui.controllers.Admin;


import easv.dk.belsign.be.User;
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

    @FXML private Button cancelBtn;
    @FXML private Label actionLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;

    @FXML private Button continueBtn;
    @FXML private Button revertBtn;

    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private ComboBox roleComboBox;


    ;
    private static final UserModel userModel = new UserModel();
    private AdminController adminController; // field for parent controller
    private User user;


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

    // Setter to set the parent Manage Users controller
    public void setManageUsersController(AdminController adminController) {
        this.adminController = adminController;
    }
    /// TODO Method reused in all controllers, lets simplify it
    public void onClickLogoutBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.TITLE_SCREEN);

        System.out.println("CreateUserController.onClickLogoutBtn");
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


    public void onClickContinueBtn(ActionEvent actionEvent) {
        System.out.println("CreateUserController.onClickContinueBtn");

        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        Object selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || (!isEditMode && rawPassword.isEmpty())) {
            System.out.println("Fail to process user because one or more fields are empty");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println("Invalid email, please enter a valid email address.");
            return;
        }

        if (selectedRole == null) {
            System.err.println("No role selected.");
            return;
        }
/// Todo Create reference to name
        String roleName = selectedRole.toString();
        int roleId = switch (roleName) {
            case "Admin" -> 1;
            case "QA Employee" -> 2;
            case "Operator" -> 3;
            default -> {
                System.err.println("Unknown role selected.");
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
                System.out.println("User updated.");
                navigateBack(); //
            } else {
                User newUser = new User(0, username, finalPassword, "", fullName, email, roleId, null, null, true, roleName);
                userModel.createNewUser(newUser);
                currentUserAfterCreate = newUser;
                System.out.println("User created.");

                enterEditMode();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void onClickCancelBtn(ActionEvent actionEvent) {
        if (!isEditMode) {
            // Cancel from create mode → exit
            navigateBack();
        } else {
            if (fieldsChanged) {
                // Revert changes
                onClickRevert();
            } else {
                // Just close
                navigateBack();
            }
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
