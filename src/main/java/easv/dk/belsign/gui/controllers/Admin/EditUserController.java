package easv.dk.belsign.gui.controllers.Admin;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.controllers.TopBarController;
import easv.dk.belsign.utils.AlertUtil;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.models.UserModel;
import easv.dk.belsign.utils.PasswordUtils;
import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditUserController implements Initializable {

    @FXML public TextField roleField;
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
    private User loggedInUser;
    @FXML
    private AnchorPane topBarHolder;
    private TopBarController topBarController;


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
            addFieldListeners();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addFieldListeners() {
        fullNameField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
    }

    private void checkIfChanged() {
        fieldsChanged =
                !fullNameField.getText().equals(originalUser.getFullName()) ||
                        !passwordField.getText().isBlank();

        continueBtn.setText(fieldsChanged ? "Update" : "Save");
        cancelBtn.setText(fieldsChanged ? "Revert" : "Close");
    }

    public void setManageUsersController(AdminController adminController) {
        this.adminController = adminController;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        if (topBarController != null) {
            topBarController.setLoggedInUser(user);
        }
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
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        String roleName = roleField.getText();

        if (fullName.isEmpty() || email.isEmpty() || (user == null && rawPassword.isEmpty())) {
            AlertUtil.error(
                    ((Node) actionEvent.getSource()).getScene(),
                    "Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|dk)$")) {
            AlertUtil.error(
                    ((Node) actionEvent.getSource()).getScene(),
                    "Invalid Email Address, Please fill in all required fields.");
            return;
        }

        int roleId = switch (roleName) {
            case "Admin" -> 1;
            case "QA Employee" -> 2;
            case "Operator" -> 3;
            default -> {
                AlertUtil.error(
                        ((Node) actionEvent.getSource()).getScene(),
                        "Unknown Role, Invalid role selected.");
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.error(
                    ((Node) actionEvent.getSource()).getScene(),
                    "Error, fail to update user.");
            return;
        }
        navigateBack();
        Platform.runLater(() ->
                AlertUtil.success(
                        ViewManager.INSTANCE.getSceneManager()
                                .getCurrentStage()
                                .getScene(),
                        "User updated ✓"));
    }

    private void navigateBack() {
        Pair<Parent, AdminController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.ADMIN_DASHBOARD);
        AdminController controller = pair.getValue();
        controller.setLoggedInUser(loggedInUser);

        Navigation.goToAdminView(pair.getKey());
    }


    public void setUserData(User user) {
        this.user = user;
        this.originalUser = user;
        fullNameField.setText(user.getFullName());
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