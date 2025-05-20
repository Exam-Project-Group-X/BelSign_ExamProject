package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.gui.ViewManagement.*;
import javafx.scene.control.*;
import javafx.util.Pair;
import easv.dk.belsign.be.Order;
import easv.dk.belsign.be.User;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QAEmployeeController implements Initializable {
    public Label welcomeLabel;
    @FXML
    private ComboBox<String> statusFilter;
    public TextField searchField;
    private List<Order> filteredOrders;

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
    @FXML
    private VBox cardContainer;

    @FXML
    private Button logoutButton;

    private static final int PAGE_SIZE = 3;
    private static final int TOGGLE_COUNT = 5;// cards per page
    private int currentPage = 1;
    private int pageCount  = 1;
    private List<Order> orders;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private final QAEmployeeModel qamodel = new QAEmployeeModel();
    private User loggedInUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            orders = qamodel.getAllOrders();
            setupStatusFilter();
            setupSearchAndFilterListeners();
            applyFilters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupStatusFilter() {
        statusFilter.getItems().clear();
        statusFilter.getItems().addAll("All", "Pending", "Complete");
        statusFilter.getSelectionModel().select("Pending");
    }

    private void setupSearchAndFilterListeners() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue() == null ? "All Statuses" : statusFilter.getValue().toString();

        List<Order> filtered = orders.stream()
                .filter(order -> String.valueOf(order.getOrderNumber()).toLowerCase().contains(search))
                .filter(order -> {
                    if (selectedStatus.equals("All")) return true;
                    return order.getOrderStatus().equalsIgnoreCase(selectedStatus);
                })
                .toList();

        filteredOrders = filtered;

        pageCount = (int)Math.ceil((double)filtered.size() / PAGE_SIZE);
        currentPage = Math.min(currentPage, pageCount == 0 ? 1 : pageCount);

        loadFilteredPage(filteredOrders);
        updatePaginationToggles(filteredOrders);
    }

    private void loadFilteredPage(List<Order> list) {
        cardContainer.getChildren().clear();
        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, list.size());
        for (int i = start; i < end; i++) {
            addNewOrderCard(list.get(i));
        }
        lblPageInfo.setText("Showing page " + currentPage + " of " + pageCount);
    }



    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        // Optional: update welcome label or other UI
        welcomeLabel.setText("Welcome back, " + user.getFullName() + "!");
    }


    /// Generate QC Report button only clickable after approving ALL photos (i.e. Status "Complete"
    ///  -> Then you can Generate QC Report)
    public void loadPage(int page) {
        cardContainer.getChildren().clear();
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, orders.size());
        for (int i = start; i < end; i++) {
            addNewOrderCard(orders.get(i));
        }
        lblPageInfo.setText("Showing page " + currentPage + " of " + pageCount);
    }

    public void addNewOrderCard(Order order) {
        Pair<Parent, OrderCardController> pair = FXMLManager.INSTANCE.getFXML(FXMLPath.QA_ORDER_CARD);
        pair.getValue().setOrderData(order);
        cardContainer.getChildren().add(pair.getKey());
    }

    // Dynamically generate pagination toggle buttons based on current page
    private void updatePaginationToggles(List<Order> filtered) {
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
                loadFilteredPage(filtered);
                updatePaginationToggles(filtered);
            });
            toggleBtnContainer.getChildren().add(toggle);
        }
    }

    public void onClickLogoutBtn(ActionEvent actionEvent) {
        Navigation.goToLoginScreen();
    }

    public void onClickFirstPageBtn(ActionEvent actionEvent) {
        currentPage = 1;
        loadFilteredPage(filteredOrders);
        updatePaginationToggles(filteredOrders);
    }

    public void onClickPrevPageBtn(ActionEvent actionEvent) {
        if (currentPage > 1) {
            currentPage--;
            loadFilteredPage(filteredOrders);
            updatePaginationToggles(filteredOrders);
        }
    }

    public void onCLickNextPageBtn(ActionEvent actionEvent) {
        if (currentPage < pageCount) {
            currentPage++;
            loadFilteredPage(filteredOrders);
            updatePaginationToggles(filteredOrders);
        }
    }

    public void onClickLastPageBtn(ActionEvent actionEvent) {
        currentPage = pageCount;
        loadFilteredPage(filteredOrders);
        updatePaginationToggles(filteredOrders);
    }


}


