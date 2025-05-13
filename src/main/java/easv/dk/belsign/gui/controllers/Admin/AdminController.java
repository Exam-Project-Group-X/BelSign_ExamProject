package easv.dk.belsign.gui.controllers.Admin;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.web.UserDAO;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.gui.models.UserModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
public class AdminController implements Initializable {

    @FXML
    private VBox cardContainer;

    private static final UserModel userModel = new UserModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadAllUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAllUsers() throws SQLException {
        new Thread(() -> {
            try {
                List<User> allUsers = userModel.getAllUsers();
                //System.out.println("Loaded users: " + allUsers.size());
                if (allUsers.isEmpty()) {
                    //System.err.println("No users found in the database!");
                }
                Platform.runLater(() -> {
                    cardContainer.getChildren().clear();
                    for (User user : allUsers) {
                        addUserCard(user);
                    }
                });
            } catch (SQLException e) {
                System.err.println("Error loading users from database.");
                e.printStackTrace();
            }
        }).start();
    }

    public void addUserCard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.USER_CARD));
            Node userCard = loader.load();
            // Debug to ensure the user card loads
            //System.out.println("Loaded user card for: " + user.getFullName());
            UserCardController userCardController = loader.getController();
            userCardController.setUserData(user);
            userCardController.setParentController(this);
            cardContainer.getChildren().add(userCard);
        } catch (IOException e) {
            //System.err.println("Failed to load user card for: " + user.getFullName());
            e.printStackTrace();
        }
    }
// TODO - warning: This method is reused in a lot of controllers
    public void onClickLogoutBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.TITLE_SCREEN);
    }

    public void onClickCreateUserBtn(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.CREATE_USER));
            Parent root = loader.load();
            // Close current stage
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            //System.err.println("Failed to load user card for: " + user.getFullName());
            e.printStackTrace();
        }
    }

}


