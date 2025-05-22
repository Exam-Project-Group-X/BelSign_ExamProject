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
    @FXML private Label lblRole;
    @FXML private Label lblName;
    @FXML private Label lblEmail;
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
    public void onClickDeleteUser(ActionEvent actionEvent) {
        Window owner = ((Node) actionEvent.getSource()).getScene().getWindow();
        try {
            model.deleteUser(user);
            // Notify parent controller to refresh the cards if available
            if (adminController != null) {
                adminController.loadAllUsers();
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

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
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
