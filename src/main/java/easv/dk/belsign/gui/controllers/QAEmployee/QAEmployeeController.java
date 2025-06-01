package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.gui.ViewManagement.*;
import easv.dk.belsign.gui.controllers.TopBarController;
import easv.dk.belsign.gui.models.PhotosModel;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
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
import java.util.*;
import java.util.stream.Collectors;

public class QAEmployeeController implements Initializable {
    @FXML
    private ComboBox<String> statusFilter;
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

    @FXML
    private AnchorPane topBarHolder;
    private TopBarController topBarController;
    public TextField searchField;

    private static final int PAGE_SIZE = 5; //cards per page
    private static final int TOGGLE_COUNT = 5;// toggle btns per line
    private int currentPage = 1;
    private int pageCount  = 1;

    private static String lastSelectedStatus = "Pending";
    private static String lastSearchText = "";
    private List<Order> orders;
    private List<Order> filteredOrders;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private final QAEmployeeModel qamodel = new QAEmployeeModel();
    private final PhotosModel photosModel = new PhotosModel();
    private User loggedInUser;
    private final Map<Integer,Integer> photoCntCache = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.TOP_BAR));
            Node topBar = loader.load();
            topBarController = loader.getController();
            topBarHolder.getChildren().setAll(topBar);
            setupStatusFilter();
            setupSearchAndFilterListeners();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupStatusFilter() {
        statusFilter.getItems().clear();
        statusFilter.getItems().addAll("All", "Pending", "Complete");
        // Restore the last used filter
        if (lastSelectedStatus != null && statusFilter.getItems().contains(lastSelectedStatus)) {
            statusFilter.getSelectionModel().select(lastSelectedStatus);
        } else {
            statusFilter.getSelectionModel().select("Pending");
        }
    }

    private final PauseTransition searchPause = new PauseTransition(
            Duration.millis(250));          // ¼-second debounce

    private void setupSearchAndFilterListeners() {
        searchField.textProperty().addListener((obs, o, n) -> {
            lastSearchText = n;
            /* restart timer */
            searchPause.playFromStart();
        });
        searchPause.setOnFinished(e -> runFilterAsync());
        statusFilter.valueProperty().addListener((obs, o, n) -> {
            lastSelectedStatus = n;
            runFilterAsync();
        });
    }

    private void runFilterAsync() {                       // heavy work → Task
        Task<List<Order>> t = new Task<>() {
            @Override protected List<Order> call() {
                String search  = lastSearchText == null ? "" :
                        lastSearchText.toLowerCase();
                String status  = lastSelectedStatus == null ? "All" :
                        lastSelectedStatus;
                return orders.parallelStream()             // use all cores
                        .filter(o -> String.valueOf(o.getOrderNumber())
                                .toLowerCase().contains(search))
                        .filter(o -> status.equals("All") ||
                                o.getOrderStatus().equalsIgnoreCase(status))
                        .sorted((a, b) -> {                // purely in-memory
                            boolean readyA = a.getOrderStatus().equals("Pending") &&
                                    photoCntCache.getOrDefault(
                                            a.getOrderID(), 0) > 0;
                            boolean readyB = b.getOrderStatus().equals("Pending") &&
                                    photoCntCache.getOrDefault(
                                            b.getOrderID(), 0) > 0;
                            return Boolean.compare(!readyA, !readyB);
                        })
                        .toList();
            }
        };
        t.setOnSucceeded(ev -> {
            filteredOrders = t.getValue();
            pageCount      = (int) Math.ceil(
                    filteredOrders.size() / (double) PAGE_SIZE);
            currentPage    = Math.min(currentPage, Math.max(pageCount, 1));
            loadFilteredPage(filteredOrders);
            updatePaginationToggles(filteredOrders);
        });
        new Thread(t, "filter-orders").start();
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
                .sorted((o1, o2) -> {
                    int photos1 = photoCntCache.getOrDefault(o1.getOrderID(), 0);
                    int photos2 = photoCntCache.getOrDefault(o2.getOrderID(), 0);
                    boolean ready1 = o1.getOrderStatus().equals("Pending") && photos1 > 0;
                    boolean ready2 = o2.getOrderStatus().equals("Pending") && photos2 > 0;
                    return Boolean.compare(!ready1, !ready2); // put ‘ready’ first
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
        if (topBarController != null) topBarController.setLoggedInUser(user);
        /*  run the DB work on a background thread  */
        Task<Map<Integer,Integer>> preload = new Task<>() {
            @Override protected Map<Integer,Integer> call() throws Exception {
                orders = qamodel.getAllOrders();        // 1 ×
                Set<Integer> ids = orders.stream()
                        .map(Order::getOrderID)
                        .collect(Collectors.toSet());
                /* 1 ×  (instead of n ×)  */
                return photosModel.countPhotosForOrders(ids);
            }
        };
        preload.setOnSucceeded(ev -> {
            photoCntCache.putAll(preload.getValue());   // fill cache
            applyFilters();                             // build the UI
        });
        preload.setOnFailed(ev -> preload.getException().printStackTrace());
        new Thread(preload, "preload-photos").start();
    }

    public void addNewOrderCard(Order order) {
        Pair<Parent,OrderCardController> p =
                FXMLManager.INSTANCE.getFXML(FXMLPath.QA_ORDER_CARD);
        OrderCardController c = p.getValue();
        int cnt = photoCntCache.getOrDefault(order.getOrderID(), 0);
        c.setLoggedInUser(loggedInUser);
        c.setPhotosModel(photosModel);                 // still needed later
        c.setOrderData(order, cnt);                    // <-- overload with count
        cardContainer.getChildren().add(p.getKey());
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
        lastSelectedStatus = "Pending";
        lastSearchText = "";
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


