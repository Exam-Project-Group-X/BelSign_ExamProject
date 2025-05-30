package easv.dk.belsign.gui.controllers.Admin;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.controllers.TopBarController;
import easv.dk.belsign.gui.util.AlertUtil;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.models.UserModel;
import easv.dk.belsign.bll.util.PasswordUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

public class EditUserController implements Initializable {

    @FXML public TextField roleField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button cancelBtn;
    @FXML private Button continueBtn;

    private static final UserModel userModel = new UserModel();
    private User user;
    private User originalUser;
    private User loggedInUser;
    @FXML
    private AnchorPane topBarHolder;
    private TopBarController topBarController;
    private AdminController adminController;
    private boolean fieldsChanged = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTopBar();
        addFieldListeners();
    }

    private void loadTopBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.TOP_BAR));
            Node topBar = loader.load();
            topBarController = loader.getController();
            topBarHolder.getChildren().setAll(topBar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFieldListeners() {
        fullNameField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> checkIfChanged());
    }

    private void checkIfChanged() {
        fieldsChanged = !fullNameField.getText().equals(originalUser.getFullName()) ||
                !passwordField.getText().isBlank();
        continueBtn.setText(fieldsChanged ? "Update" : "Save");
        cancelBtn.setText(fieldsChanged ? "Revert" : "Close");
    }

    public void setManageUsersController(AdminController parentController) {
        this.adminController = parentController;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        if (topBarController != null) {
            topBarController.setLoggedInUser(user);
        }
    }

    /** Pre‑loads the form with the selected user’s details. */
    public void setUserData(User user) {
        this.originalUser = user;
        roleField.setText(user.getRoleName());
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        passwordField.clear();

        emailField.setDisable(true);          // e‑mail & role are immutable here
        roleField.setDisable(true);

        continueBtn.setText("Save");
        cancelBtn.setText("Close");
    }

    public void onClickCancelBtn(ActionEvent actionEvent) {
        if (fieldsChanged) {
            // revert to original snapshot
            setUserData(originalUser);
            fieldsChanged = false;
            checkIfChanged();
        } else {
            navigateBack();
        }
    }

    public void onClickContinueBtn(ActionEvent actionEvent) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = passwordField.getText().trim();
        String roleName = roleField.getText();

        if (fullName.isEmpty() || email.isEmpty()) {
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
        Integer roleId = allRoles.get(roleName);
        if (roleId == null) {
            AlertUtil.error(
                    ((Node) actionEvent.getSource()).getScene(),
                    "Unknown role selected.");
            return;
        }

        String finalPassword = newPassword.isBlank()
                ? originalUser.getPasswordHash()     // keep old hash
                : PasswordUtils.hashPassword(newPassword);     // store new hash

        User updatedUser = new User(
                originalUser.getUserID(),
                finalPassword,
                "",
                fullName,
                email,
                roleId,
                null, null,
                true,
                roleName
        );

        try {
            userModel.updateUser(updatedUser);
            originalUser  = updatedUser;          // refresh snapshot
            fieldsChanged = false;
            navigateBack();
            Platform.runLater(() -> AlertUtil.success(
                    ViewManager.INSTANCE.getSceneManager()
                            .getCurrentStage()
                            .getScene(),
                    "User updated ✓"));
        } catch (SQLException ex) {
            ex.printStackTrace();
            AlertUtil.error(
                    ((Node) actionEvent.getSource()).getScene(),
                    "Error, fail to update user.");
        }
    }

    private static final Map<String, Integer> allRoles = Map.of(
            "Admin",       1,
            "QA Employee", 2,
            "Operator",    3
    );

    private void navigateBack() {
        Pair<Parent, AdminController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.ADMIN_DASHBOARD);
        AdminController controller = pair.getValue();
        controller.setLoggedInUser(loggedInUser);

        Navigation.goToAdminView(pair.getKey());
    }
}