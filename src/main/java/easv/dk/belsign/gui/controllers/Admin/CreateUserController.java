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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CreateUserController implements Initializable {


    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private ComboBox roleComboBox;

    private static final UserModel userModel = new UserModel();
    private AdminController adminController; // field for parent controller
    private User user;

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

    public void onClickCancelBtn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.ADMIN_DASHBOARD));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("CreateUserController.onClickCancelBtn");
    }

    public void onClickContinueBtn(ActionEvent actionEvent) {

        System.out.println("CreateUserController.onClickContinueBtn");

        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        Object selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || (user == null && rawPassword.isEmpty())) {
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

        String roleName = selectedRole.toString();
        int roleId = roleName.equals("Admin") ? 1 : 2;

        String finalPassword = (user != null && user.getUserID() > 0)
                ? getFinalPassword(rawPassword, user.getPasswordHash())  // Edit
                : PasswordUtils.hashPassword(rawPassword);               // New

        try {
            if (this.user != null && this.user.getUserID() > 0) {
                // Edit existing user
                User updatedUser = new User(user.getUserID(), username, finalPassword, "", fullName, email, roleId, null, null, true, roleName);
                userModel.updateUser(updatedUser);
            } else {
                // Create new user
                User newUser = new User(0, username, finalPassword, "", fullName, email, roleId, null, null, true, roleName);
                userModel.createNewUser(newUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.ADMIN_DASHBOARD));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
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


    public void setUserData(User user) {
//        this.user = user;
//        usernameField.setText(user.getUsername());
//        fullNameField.setText(user.getFullName());
//        emailField.setText(user.getEmail());
//        passwordField.setText("");
//        roleComboBox.setValue(user.getRoleName());
    }
}
