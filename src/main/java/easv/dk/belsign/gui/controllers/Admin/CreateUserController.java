package easv.dk.belsign.gui.controllers.Admin;


import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.controllers.TopBarController;
import easv.dk.belsign.utils.AlertUtil;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
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
import javafx.util.Pair;

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
    @FXML
    private AnchorPane topBarHolder;
    private TopBarController topBarController;

    private User loggedInUser;


    private static final UserModel userModel = new UserModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.TOP_BAR));
            Node topBar = loader.load();
            topBarController = loader.getController();
            topBarHolder.getChildren().setAll(topBar);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObservableList<String> roles = userModel.getAllRoleNames();
            roleComboBox.setItems(roles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void onClickContinueBtn(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        Object selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (fullName.isEmpty() || email.isEmpty() || rawPassword.isEmpty()) {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|dk)$")) {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Invalid Email Address, Please fill in all required fields.");
            return;
        }

        if (selectedRole == null) {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Please select a role.");
            return;
        }

        String roleName = selectedRole.toString();
        int roleId = switch (roleName) {
            case "Admin" -> 1;
            case "QA Employee" -> 2;
            case "Operator" -> 3;
            default -> {
                AlertUtil.error(
                        ((Node) event.getSource()).getScene(),
                        "Invalid role selected.");
                yield -1;
            }
        };
        if (roleId == -1) return;

        String hashedPassword = PasswordUtils.hashPassword(rawPassword);

        try {
            for (User existingUser : userModel.getAllUsers()) {
                if (existingUser.getEmail().equalsIgnoreCase(email)) {
                    AlertUtil.error(
                            ((Node) event.getSource()).getScene(),
                            "A user with this email already exists.");
                    return;
                }
            }

            User newUser = new User(0, hashedPassword, "", fullName, email, roleId, null, null, true, roleName);
            userModel.createNewUser(newUser);
            navigateBack();
            Platform.runLater(() ->
                    AlertUtil.success(
                            ViewManager.INSTANCE.getSceneManager()
                                    .getCurrentStage()
                                    .getScene(),
                            "User created ✓"));

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Error, failed to save user.");
        }
    }

    public void onClickCancelBtn(ActionEvent event) {
        navigateBack();
    }

    private void navigateBack() {
        Pair<Parent, AdminController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.ADMIN_DASHBOARD);
        AdminController controller = pair.getValue();
        controller.setLoggedInUser(loggedInUser);

        Navigation.goToAdminView(pair.getKey());
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        if (topBarController != null) {
            topBarController.setLoggedInUser(user);
        }
    }

}