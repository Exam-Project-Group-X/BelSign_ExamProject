    package easv.dk.belsign.gui.controllers.Admin;
    import easv.dk.belsign.be.Order;
    import easv.dk.belsign.be.User;
    import easv.dk.belsign.gui.ViewManagement.FXMLManager;
    import easv.dk.belsign.gui.ViewManagement.FXMLPath;
    import easv.dk.belsign.gui.ViewManagement.Navigation;
    import easv.dk.belsign.gui.ViewManagement.ViewManager;
    import easv.dk.belsign.gui.controllers.TopBarController;
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
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.VBox;
    import javafx.stage.Modality;
    import javafx.stage.Stage;
    import javafx.util.Pair;

    import java.io.IOException;
    import java.net.URL;
    import java.sql.SQLException;
    import java.util.List;
    import java.util.ResourceBundle;
    public class AdminController implements Initializable {
        @FXML private Button firstPageBtn, prevPageBtn, nextPageBtn, lastPageBtn;
        @FXML private HBox toggleBtnContainer;
        @FXML private Label lblPageInfo;
        @FXML private ComboBox<String> roleFilter;
        @FXML private TextField searchField;
        @FXML private VBox cardContainer;

        @FXML
        private AnchorPane topBarHolder;
        private TopBarController topBarController;
        private User loggedInUser;

        private ToggleGroup toggleGroup = new ToggleGroup();
        private static final UserModel userModel = new UserModel();
        private List<User> allUsersList;

        private static final int PAGE_SIZE = 8; //cards per page
        private static final int TOGGLE_COUNT = 2;// toggle btn per line
        private int currentPage = 1;
        private int pageCount  = 1;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            loadTopBar();
            fetchUsersAsync();      // Loads users -> init dropdowns & first page
            setupListeners();
        }

        private void loadTopBar() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.TOP_BAR));
                Node topBar = loader.load();
                topBarController = loader.getController();
                topBarHolder.getChildren().setAll(topBar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void setupListeners() {
            searchField.textProperty().addListener((obs, o, n) -> applyFilters());
            roleFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        }

        private void fetchUsersAsync() {
            new Thread(() -> {
                try {
                    List<User> users = userModel.getAllUsers();
                    Platform.runLater(() -> {
                        allUsersList = users;
                        initRoleFilter();
                        applyFilters();
                    });
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }

        public void refreshUsers() { fetchUsersAsync(); }

        /* ───────────────────────────── Filtering / Search ───────────────────────────── */
        private void initRoleFilter() {
            ObservableList<String> roles = FXCollections.observableArrayList("Clear", "All Roles");
            roles.addAll(allUsersList.stream()
                    .map(User::getRoleName)
                    .distinct()
                    .collect(Collectors.toList()));
            roleFilter.setItems(roles);
            roleFilter.getSelectionModel().select("All Roles");
        }

        private void applyFilters() {
            if (allUsersList == null) {
                return; // No users to filter
            }
            if (allUsersList == null) return;   // Safety‑net during early init

            String search       = blankIfNull(searchField.getText()).toLowerCase();
            String selectedRole = normalizeRoleValue(roleFilter.getValue());

            List<User> filtered = allUsersList.stream()
                    .filter(u -> {
                        String role = blankIfNull(u.getRoleName()).toLowerCase();
                        return u.getFullName().toLowerCase().contains(search) ||
                                u.getEmail().toLowerCase().contains(search)    ||
                                role.contains(search);                         // return match role text
                    })
                    .filter(u -> "all roles".equals(selectedRole) ||
                            u.getRoleName().toLowerCase().contains(selectedRole))
                    .collect(Collectors.toList());

            updatePageCount(filtered.size());
            loadPage(filtered);
            updatePaginationToggles();
        }
        private static String blankIfNull(String s) { return s == null ? "" : s; }
        private String normalizeRoleValue(String role) {
            if ("Clear".equalsIgnoreCase(role)) {
                roleFilter.getSelectionModel().select("All Roles");
                return "all roles";
            }
            return blankIfNull(role).toLowerCase();
        }

        /* ───────────────────────────────── Pagination ───────────────────────────────── */
        private void updatePageCount(int totalItems) {
            pageCount = Math.max(1, (int) Math.ceil(totalItems / (double) PAGE_SIZE));
            currentPage = Math.min(currentPage, pageCount);
            updateNavButtonsVisibility();
        }

        /** Hides nav buttons when there is only a single page to display. */
        private void updateNavButtonsVisibility() {
            boolean multiPage = pageCount > 1;
            firstPageBtn.setVisible(multiPage);
            prevPageBtn.setVisible(multiPage);
            nextPageBtn.setVisible(multiPage);
            lastPageBtn.setVisible(multiPage);

            // Also remove them from layout to avoid empty space
            firstPageBtn.setManaged(multiPage);
            prevPageBtn.setManaged(multiPage);
            nextPageBtn.setManaged(multiPage);
            lastPageBtn.setManaged(multiPage);
        }

        private void loadPage(List<User> source) {
            cardContainer.getChildren().clear();

            int start = (currentPage - 1) * PAGE_SIZE;
            int end   = Math.min(start + PAGE_SIZE, source.size());

            source.subList(start, end).forEach(this::addUserCard);
            lblPageInfo.setText("Showing page " + currentPage + " of " + pageCount);
        }

        // Dynamically generate pagination toggle buttons based on current page
        private void updatePaginationToggles() {
            toggleBtnContainer.getChildren().clear();
            if (pageCount <= 1) return;   // nothing to build when single page

            int startPage = ((currentPage - 1) / TOGGLE_COUNT) * TOGGLE_COUNT + 1;
            int endPage   = Math.min(startPage + TOGGLE_COUNT - 1, pageCount);

            for (int p = startPage; p <= endPage; p++) {
                ToggleButton toggle = new ToggleButton(String.valueOf(p));
                toggle.setToggleGroup(toggleGroup);
                toggle.setSelected(p == currentPage);
                if (p == currentPage) {
                    toggle.setStyle("-fx-background-color: #004884; -fx-text-fill: white;");
                }
                toggle.setOnAction(e -> {
                    currentPage = Integer.parseInt(toggle.getText());
                    applyFilters();
                });
                toggleBtnContainer.getChildren().add(toggle);
            }
        }

        @FXML private void onClickFirstPageBtn(ActionEvent e) { currentPage = 1; applyFilters(); }
        @FXML private void onClickPrevPageBtn(ActionEvent e)  { if (currentPage > 1) { currentPage--; applyFilters(); } }
        @FXML private void onCLickNextPageBtn(ActionEvent e)  { if (currentPage < pageCount) { currentPage++; applyFilters(); } }
        @FXML private void onClickLastPageBtn(ActionEvent e)  { currentPage = pageCount; applyFilters(); }

        public void setLoggedInUser(User user) {
            this.loggedInUser = user; //Also sets logged in for other controllers
            if (topBarController != null) {
                topBarController.setLoggedInUser(user);
            }
        }

        public User getLoggedInUser() {
            return loggedInUser;
        }

        public void addUserCard(User user) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.USER_CARD));
                Node userCard = loader.load();
                UserCardController userCardController = loader.getController();
                userCardController.setUserData(user);
                userCardController.setParentController(this);
                userCardController.setLoggedInUser(loggedInUser);
                cardContainer.getChildren().add(userCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onClickCreateUserBtn(ActionEvent actionEvent) {
            Pair<Parent, CreateUserController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.CREATE_USER);
            CreateUserController controller = pair.getValue();
            controller.setLoggedInUser(loggedInUser);
            Navigation.goToCreateUserView(pair.getKey());
        }
    }