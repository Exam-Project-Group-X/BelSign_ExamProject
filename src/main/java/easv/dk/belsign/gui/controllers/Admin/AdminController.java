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
        setupTableColumns();
        setupSearchAndFilter();
        addUserButton.setOnAction(e -> handleAddUser());
        refreshUserList();
    }

    /**
     * Initializes and configures all table columns including action buttons.
     */
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        emailColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUserRole()));

        createdAtColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCreatedAt() != null
                        ? cell.getValue().getCreatedAt().toString() : "")
        );
        updatedAtColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getUpdatedAt() != null
                        ? cell.getValue().getUpdatedAt().toString() : "")
        );

        // Add Edit and Delete buttons to each row
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox hbox = new HBox(10, editButton, deleteButton);

            {
                hbox.setAlignment(Pos.CENTER);
                editButton.setOnAction(e -> openEditUserWindow(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    userDAO.deleteUser(user);
                    searchField.clear(); // Reset search and filters
                    roleFilter.setValue("All Roles");
                    refreshUserList();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        userTable.setEditable(false);
    }

    /**
     * Populates and binds the search field and role filter dropdown.
     */
    private void setupSearchAndFilter() {
        if (roleFilter.getItems().isEmpty()) {
            roleFilter.getItems().addAll("All Roles", "Admin", "QA", "Operator");
        }
        roleFilter.setValue("All Roles");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        roleFilter.setOnAction(e -> applyFilters());
    }

    /**
     * Filters the user list based on search text and selected role.
     */
    private void applyFilters() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String selectedRole = roleFilter.getValue();

        ObservableList<User> filtered = FXCollections.observableArrayList();

        for (User user : masterUserList) {
            boolean matchesSearch = user.getUsername().toLowerCase().contains(search)
                    || user.getEmail().toLowerCase().contains(search);
            boolean matchesRole = selectedRole == null || selectedRole.equals("All Roles")
                    || user.getUserRole().equalsIgnoreCase(selectedRole);

            if (matchesSearch && matchesRole) {
                filtered.add(user);
            }
        }

        userTable.setItems(filtered);
        userTable.refresh();
    }

    /**
     * Reloads the list of users from the database and applies filters.
     */
    public void refreshUserList() {
        masterUserList.setAll(userDAO.getAllUsers());
        applyFilters();
    }

    /**
     * Opens the add-user form.
     */
    @FXML
    private void handleAddUser() {
        openAddUserWindow(null);
    }

    /**
     * Opens a modal window to add or edit a user.
     */
    private void openAddUserWindow(User userToEdit) {
        Stage userStage = new Stage();
        userStage.initModality(Modality.APPLICATION_MODAL);
        userStage.setTitle(userToEdit != null ? "Edit User" : "Add User");

        // Input fields
        TextField usernameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField visiblePasswordField = new TextField();
        CheckBox showPasswordCheckBox = new CheckBox("Show password");

        // Bind password visibility toggle
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
        visiblePasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        visiblePasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        // Role dropdown
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "QA", "Operator");

        // Populate fields if editing
        if (userToEdit != null) {
            usernameField.setText(userToEdit.getUsername());
            emailField.setText(userToEdit.getEmail());
            passwordField.setText(userToEdit.getPasswordHash());
            visiblePasswordField.setText(userToEdit.getPasswordHash());
            roleBox.setValue(userToEdit.getUserRole());
        }

        // Save button
        Button saveBtn = new Button(userToEdit != null ? "Save Changes" : "Add User");
        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleBox.getValue();

            // Validate input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                    || (userToEdit == null && role == null)) {
                showAlert("Please fill in all fields");
                return;
            }

            if (!email.contains("@")) {
                showAlert("Invalid email address");
                return;
            }

            if (userToEdit == null && isUsernameAlreadyUsed(username)) {
                showAlert("Username already exists");
                return;
            }

            // Save or update user
            if (userToEdit != null) {
                userToEdit.setUsername(username);
                userToEdit.setEmail(email);
                userToEdit.setPasswordHash(password);
                userToEdit.setUserRole(role);
                userDAO.updateUser(userToEdit);
            } else {
                userDAO.addUser(new User(username, email, password, role));
            }

            refreshUserList();
            userStage.close();
        });

        // Layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Email:"), emailField,
                new Label("Password:"), passwordField, visiblePasswordField, showPasswordCheckBox,
                new Label("Role:"), roleBox,
                saveBtn
        );

        Scene scene = new Scene(vbox, 320, 400);
        userStage.setScene(scene);
        userStage.showAndWait();
    }

    /**
     * Opens the edit window for a selected user.
     */
    private void openEditUserWindow(User user) {
        openAddUserWindow(user);
    }

    /**
     * Checks whether a username is already in use (case-insensitive).
     */
    private boolean isUsernameAlreadyUsed(String username) {
        return userDAO.getAllUsers().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    /**
     * Displays an information alert with the given message.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    public void onClickLogoutBtn(ActionEvent actionEvent) {

    }
}