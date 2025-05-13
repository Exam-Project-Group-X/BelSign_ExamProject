package easv.dk.belsign.gui.controllers.Admin;
import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.models.UserModel;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;
public class UserCardController {
    @FXML private Label lblRole;
    @FXML private Label lblName;
    @FXML private Label lblEmail;
    private User user;
    private AdminController adminController;
    private static final UserModel model = new UserModel();
    public void setUserData(User user) {
        this.user = user;
        lblRole.setText(user.getRoleName());
        lblName.setText(user.getFullName());
        lblEmail.setText(user.getEmail());
    }
    public void setParentController(AdminController adminController) {
        this.adminController = adminController;
    }
    public void onClickDeleteUser(ActionEvent actionEvent) {
        try {
            model.deleteUser(user);
            // Notify parent controller to refresh the cards if available
            if (adminController != null) {
                adminController.loadAllUsers();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void onClickEditUser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.USER_EDITOR));
            Parent root = loader.load();
            EditUserController userEditorController = loader.getController();
            userEditorController.setUserData(user);
            userEditorController.setManageUsersController(adminController);

            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
}}
