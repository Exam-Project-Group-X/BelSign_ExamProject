package easv.dk.belsign.gui.controllers.Admin;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.web.UserDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button addUserButton;
    @FXML private Button logoutButton;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private TableColumn<User, String> updatedAtColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    private final ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {

    }

    public void onClickLogoutBtn(ActionEvent actionEvent) {
    }

    /**
     * Initializes and configures all table columns including action buttons.
     */

}
