package easv.dk.belsign.gui.controllers.Admin;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.models.UserModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class UserEditorController {

    @FXML private TextField usernameField;


    @FXML private TextField fullNameField;


    @FXML private TextField emailField;


    @FXML private TextField passwordField;

    @FXML private ComboBox roleComboBox;


    private static final UserModel userModel = new UserModel();


    public void onClickLogoutBtn(ActionEvent actionEvent) {

        Platform.exit();

    }


    public void onClickCancelBtn(ActionEvent actionEvent) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.ADMIN_DASHBOARD));

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));

        } catch (IOException e) {


            e.printStackTrace();

        }
    }


    public void onClickContinueBtn(ActionEvent actionEvent) {

        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = (String) roleComboBox.getValue();

/*        int roleId = role.equals("Admin") ? 1 : 2;
        // Create a new User instance. Adjust the constructor parameters if needed.
        User user = new User(0, username, password, "", fullName, email, roleId, null, null, true, role);
        try {
            userModel.createNewUser(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
    }
}
