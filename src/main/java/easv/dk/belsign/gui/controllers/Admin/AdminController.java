package easv.dk.belsign.gui.controllers.Admin;
import easv.dk.belsign.be.Order;
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
    @FXML
    private HBox toggleBtnContainer;
    @FXML
    private Button firstPageBtn;
    @FXML
    private Button lastPageBtn;
    @FXML
    private Label lblPageInfo;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchField;
    @FXML private VBox cardContainer;

    private static final int PAGE_SIZE = 6;
    private static final int TOGGLE_COUNT = 5;// cards per page
    private int currentPage = 1;
    private int pageCount  = 1;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private static final UserModel userModel = new UserModel();
    private List<User> allUsersList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Get all users from the UserModel
            allUsersList = userModel.getAllUsers();
            loadPage(currentPage);
            pageCount = (int)Math.ceil((double)allUsersList.size() / PAGE_SIZE);
            //updatePageCount(allUsersList.size());
            updatePaginationToggles();
            loadAllUsers();
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers());
            statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterUsers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePageCount(int totalItems) {
        pageCount = (int) Math.ceil(totalItems / (double) PAGE_SIZE);

        if (currentPage > pageCount) currentPage = pageCount;
    }

    public void loadPage(int page) {
        cardContainer.getChildren().clear();
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allUsersList.size());
        for (int i = start; i < end; i++) {
            addUserCard(allUsersList.get(i));
        }
        lblPageInfo.setText("Showing page " + currentPage + " of " + pageCount);
    }

    // Dynamically generate pagination toggle buttons based on current page
    private void updatePaginationToggles() {
        toggleBtnContainer.getChildren().clear();
        int startPage = ((currentPage - 1) / TOGGLE_COUNT) * TOGGLE_COUNT + 1;
        int endPage = Math.min(startPage + TOGGLE_COUNT - 1, pageCount);

        for (int i = startPage; i <= endPage; i++) {
            ToggleButton toggle = new ToggleButton(String.valueOf(i));
            toggle.setToggleGroup(toggleGroup);
            if (i == currentPage) {
                toggle.setStyle("-fx-background-color: #004884; -fx-text-fill: white;");
                toggle.setSelected(true);
            }
            int pageNum = i;
            toggle.setOnAction(e -> {
                currentPage = pageNum;
                loadPage(currentPage);
                updatePaginationToggles();
            });
            toggleBtnContainer.getChildren().add(toggle);
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

    public void onClickFirstPageBtn(ActionEvent actionEvent) {
        currentPage = 1;
        loadPage(currentPage);
        updatePaginationToggles();
    }

    public void onClickPrevPageBtn(ActionEvent actionEvent) {
        if (currentPage > 1) {
            currentPage--;
            loadPage(currentPage);
            updatePaginationToggles();
        }
    }

    public void onCLickNextPageBtn(ActionEvent actionEvent) {
        if (currentPage < pageCount) {
            currentPage++;
            loadPage(currentPage);
            updatePaginationToggles();
        }
    }

    public void onClickLastPageBtn(ActionEvent actionEvent) {
        currentPage = pageCount;
        loadPage(currentPage);
        updatePaginationToggles();
    }
}
