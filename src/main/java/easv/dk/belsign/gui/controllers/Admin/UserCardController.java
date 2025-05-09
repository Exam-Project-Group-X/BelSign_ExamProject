package easv.dk.belsign.gui.controllers.Admin;


import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserCardController {

    @FXML private Label lblRole;
    @FXML private Label lblName;
    @FXML private Label lblEmail;

    public void setUserData(User user) {

        lblRole.setText(user.getRoleName());
        lblName.setText(user.getFullName());

        lblEmail.setText(user.getEmail());

    }

    public void onClickEditUser(ActionEvent actionEvent) {


    }


    public void onClickDeleteUser(ActionEvent actionEvent) {

    }

}
