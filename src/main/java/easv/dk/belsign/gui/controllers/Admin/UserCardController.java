package easv.dk.belsign.gui.controllers.Admin;
import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLManager;
import easv.dk.belsign.utils.AlertUtil;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import easv.dk.belsign.gui.models.UserModel;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Window;
import javafx.scene.control.Label;
import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;
public class UserCardController {
    @FXML private Label lblRole, lblName, lblEmail;
    private User loggedInUser;
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
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }
    @FXML
    private void onClickDeleteUser(ActionEvent actionEvent) {
        try {
            model.deleteUser(user);
            // Ask parent controller to reload the user list
            if (adminController != null) {
                adminController.refreshUsers();
                AlertUtil.error(
                        ((Node) actionEvent.getSource()).getScene(),
                        "User deleted ✓");           }
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtil.error(
                    ((Node) actionEvent.getSource()).getScene(),
                    "Error, Failed to delete user.");
        }
    }

    public void onClickEditUser(ActionEvent actionEvent) {
        Pair<Parent, EditUserController> pair =
                FXMLManager.INSTANCE.getFXML(FXMLPath.USER_EDITOR);

        EditUserController controller = pair.getValue();
        controller.setUserData(user);
        controller.setManageUsersController(adminController);
        controller.setLoggedInUser(loggedInUser);

        Navigation.goToEditUserView(pair.getKey());
    }
}
