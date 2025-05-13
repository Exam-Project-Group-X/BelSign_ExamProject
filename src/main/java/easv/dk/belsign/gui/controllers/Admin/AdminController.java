package easv.dk.belsign.gui.controllers.Admin;
import easv.dk.belsign.be.User;
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
import java.util.stream.Collectors;
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

    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchField;
    @FXML private VBox cardContainer;

    private static final UserModel userModel = new UserModel();
    private List<User> allUsersList;
    @Override

    public void initialize(URL location, ResourceBundle resources) {

        try {
            loadAllUsers();
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers());
            statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterUsers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAllUsers() throws SQLException {

        new Thread(() -> {
            try {
                allUsersList = userModel.getAllUsers();
                Platform.runLater(() ->{
                    setUpStatusFilter();
                    filterUsers();
                });
            } catch (SQLException e) {
                System.err.println("Error loading users.");
                e.printStackTrace();
            }
        }).start();

    }
    public void addUserCard(User user) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.USER_CARD));
            Node userCard = loader.load();
            UserCardController userCardController = loader.getController();
            userCardController.setUserData(user);
            userCardController.setParentController(this);
            cardContainer.getChildren().add(userCard);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    private void setUpStatusFilter() {
        ObservableList<String> roles = FXCollections.observableArrayList("All");
        roles.addAll(
                allUsersList.stream()
                        .map(user -> user.getRoleName())
                        .distinct()
                        .collect(Collectors.toList())
        );
        statusFilter.setItems(roles);
        statusFilter.getSelectionModel().selectFirst();
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

    private void filterUsers() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String selectedRole = statusFilter.getValue() == null ? "" : statusFilter.getValue().toString().toLowerCase();
        List<User> filteredUsers = allUsersList.stream()
                .filter(user ->
                        user.getFullName().toLowerCase().contains(search) ||
                                user.getEmail().toLowerCase().contains(search) ||
                                user.getUsername().toLowerCase().contains(search))
                .filter(user -> {
                    // If no role is selected, show all users
                    if (selectedRole.isEmpty() || selectedRole.equals("all")) {
                        return true;
                    } else {
                        return user.getRoleName().toLowerCase().contains(selectedRole);
                    }
                })
                .collect(Collectors.toList());
        cardContainer.getChildren().clear();
        for (User user : filteredUsers) {
            addUserCard(user);
        }
    }
}
