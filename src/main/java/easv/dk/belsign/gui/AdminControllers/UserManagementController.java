package easv.dk.belsign.gui.AdminControllers;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.web.UserDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> roleFilter;

    @FXML
    private Button addUserButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    private final ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearchAndFilter();
        addUserButton.setOnAction(e -> handleAddUser());
        refreshUserList();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        emailColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUserRole()));

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox hbox = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(e -> openEditUserWindow(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    userDAO.deleteUser(user);
                    searchField.clear();
                    roleFilter.setValue("All Roles");
                    refreshUserList();
                });
                hbox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        userTable.setEditable(false);
    }

    private void setupSearchAndFilter() {
        if (roleFilter.getItems().isEmpty()) {
            roleFilter.getItems().addAll("All Roles", "Admin", "QA", "Operator");
        }
        roleFilter.setValue("All Roles");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        roleFilter.setOnAction(e -> applyFilters());
    }

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

    public void refreshUserList() {
        masterUserList.setAll(userDAO.getAllUsers());
        System.out.println("Loaded users: " + masterUserList.size());
        masterUserList.forEach(u -> System.out.println(u.getUsername() + " | " + u.getUserRole()));
        applyFilters();
    }

    @FXML
    private void handleAddUser() {
        openAddUserWindow(null);
    }

    private void openAddUserWindow(User userToEdit) {
        Stage userStage = new Stage();
        userStage.initModality(Modality.APPLICATION_MODAL);
        userStage.setTitle(userToEdit != null ? "Edit User" : "Add User");

        TextField usernameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField visiblePasswordField = new TextField();
        CheckBox showPasswordCheckBox = new CheckBox("Show password");

        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
        visiblePasswordField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        visiblePasswordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "QA", "Operator");

        if (userToEdit != null) {
            usernameField.setText(userToEdit.getUsername());
            emailField.setText(userToEdit.getEmail());
            passwordField.setText(userToEdit.getPasswordHash());
            visiblePasswordField.setText(userToEdit.getPasswordHash());
            roleBox.setValue(userToEdit.getUserRole());
        }

        Button saveBtn = new Button(userToEdit != null ? "Save Changes" : "Add User");
        saveBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleBox.getValue();

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

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Email:"), emailField,
                new Label("Password:"), passwordField, visiblePasswordField, showPasswordCheckBox
        );

        if (userToEdit == null) {
            vbox.getChildren().addAll(new Label("Role:"), roleBox);
        } else {
            vbox.getChildren().add(roleBox);
        }

        vbox.getChildren().add(saveBtn);

        Scene scene = new Scene(vbox, 320, 400);
        userStage.setScene(scene);
        userStage.showAndWait();
    }

    private void openEditUserWindow(User user) {
        openAddUserWindow(user);
    }

    private boolean isUsernameAlreadyUsed(String username) {
        return userDAO.getAllUsers().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}
