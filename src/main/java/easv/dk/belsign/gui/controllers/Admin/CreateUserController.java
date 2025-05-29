package easv.dk.belsign.gui.controllers.Admin;


import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.controllers.TopBarController;
import easv.dk.belsign.gui.util.AlertUtil;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CreateUserController implements Initializable {

    @FXML private Label labelRole;
    @FXML private Button cancelBtn;
    @FXML private Label actionLabel;
    @FXML private Label fullNameLabel, emailLabel, passwordLabel;
    @FXML private Button CreateBtn;
    @FXML private TextField fullNameField, emailField, passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML
    private AnchorPane topBarHolder;
    private TopBarController topBarController;
    private User loggedInUser;

    private static final UserModel userModel = new UserModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTopBar();
        fetchRoleCombo();
    }

    private void loadTopBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.TOP_BAR));
            Node topBar      = loader.load();
            topBarController = loader.getController();
            topBarHolder.getChildren().setAll(topBar);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void fetchRoleCombo() {
        try {
            ObservableList<String> roles = userModel.getAllRoleNames();
            roleComboBox.setItems(roles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onClickCreateBtn(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String rawPassword = passwordField.getText().trim();
        String roleName   = roleComboBox.getSelectionModel().getSelectedItem();
        // ---------- Validation each fields ----------
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
        if (roleName == null) {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Please select a role.");
            return;
        }
        Integer roleId = allRoles.get(roleName);
        if (roleId == null) {
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Unknown role selected.");
            return;
        }
        // ---------- Duplicate e‑mail check ----------
        try {
            List<User> existing = userModel.getAllUsers();
            boolean emailExists = existing.stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
            if (emailExists) {
                AlertUtil.error(
                        ((Node) event.getSource()).getScene(),
                        "A user with this e‑mail already exists.");
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            AlertUtil.error(
                    ((Node) event.getSource()).getScene(),
                    "Could not validate e‑mail uniqueness.");
            return;
        }

            String hashedPassword = PasswordUtils.hashPassword(rawPassword);
            User newUser = new User(0, hashedPassword, "", fullName, email, roleId, null, null, true, roleName);
        try {
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

    private static final Map<String, Integer> allRoles = Map.of(
            "Admin",       1,
            "QA Employee", 2,
            "Operator",    3
    );

    public void onClickCancelBtn(ActionEvent event) {
        navigateBack();
    }

    private void navigateBack() {
        Pair<Parent, AdminController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.ADMIN_DASHBOARD);
        pair.getValue().setLoggedInUser(loggedInUser);
        Navigation.goToAdminView(pair.getKey());
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        if (topBarController != null) {
            topBarController.setLoggedInUser(user);
        }
    }
}