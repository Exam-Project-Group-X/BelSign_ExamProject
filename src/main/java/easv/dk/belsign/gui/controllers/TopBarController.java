package easv.dk.belsign.gui.controllers;

import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.ViewManagement.Navigation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TopBarController {
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;

    public void setLoggedInUser(User user) {
        welcomeLabel.setText("Hello, " + user.getFullName() + "!");
    }

    @FXML private void onClickLogoutBtn(ActionEvent actionEvent) {
            Navigation.goToTitleScreen();
    }
}