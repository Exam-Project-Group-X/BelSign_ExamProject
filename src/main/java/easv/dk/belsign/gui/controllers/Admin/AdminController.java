    package easv.dk.belsign.gui.controllers.Admin;

    import easv.dk.belsign.be.User;
    import easv.dk.belsign.gui.ViewManagement.FXMLManager;
    import easv.dk.belsign.gui.ViewManagement.FXMLPath;
    import easv.dk.belsign.gui.ViewManagement.Navigation;
    import easv.dk.belsign.gui.controllers.TopBarController;
    import easv.dk.belsign.gui.models.UserModel;
    import javafx.application.Platform;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import java.util.stream.Collectors;
    import javafx.scene.Node;
    import javafx.scene.Parent;
    import javafx.scene.control.*;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.VBox;
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
        private ToggleGroup toggleGroup = new ToggleGroup();
        private User loggedInUser;

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
            // if the user picked “Clear” then reset both filters
            String rawRole = roleFilter.getValue();
            if ("Clear".equals(rawRole)) {
                searchField.clear();
                roleFilter.getSelectionModel().select("All Roles");
            }
            // run model’s filter (fills displayedUsers)
            userModel.filterUsers(searchField.getText(), roleFilter.getValue());
            // get the filtered list
            List<User> filtered = userModel.getDisplayedUsers();
            //  pagination calculations
            pageCount   = Math.max(1, (int)Math.ceil(filtered.size() / (double) PAGE_SIZE));
            currentPage = Math.min(currentPage, pageCount);
            updateNavButtonsVisibility();
            //  extract sublist for current page
            int start = (currentPage - 1) * PAGE_SIZE;
            int end   = Math.min(start + PAGE_SIZE, filtered.size());
            List<User> page = filtered.subList(start, end);
            // render UI
            loadPage(page);
            updatePaginationToggles();
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

        private void loadPage(List<User> page) {
            cardContainer.getChildren().clear();
            page.forEach(this::addUserCard);
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