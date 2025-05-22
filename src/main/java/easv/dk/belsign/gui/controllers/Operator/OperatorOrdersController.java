package easv.dk.belsign.gui.controllers.Operator;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.bll.OrderManager;
import easv.dk.belsign.gui.ViewManagement.*;
import easv.dk.belsign.gui.controllers.Operator.components.OperatorOrderCardController;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.List;

public class OperatorOrdersController {

    @FXML
    private VBox cardContainer;

    @FXML
    private HBox paginationBox;

    @FXML
    private Label pageInfoLabel, noOrdersLabel, errorLabel;

    @FXML
    private TextField searchField;

    private ObservableList<Order> allOrders;
    private FilteredList<Order> filteredOrders;
    private static final int ORDERS_PER_PAGE = 5;
    private int currentPage = 1;
    private int totalPages;

    private OrderManager orderManager = new OrderManager();

    @FXML
    public void initialize() {
        try {
            allOrders = FXCollections.observableArrayList(orderManager.getAllPendingOrders());
            filteredOrders = new FilteredList<>(allOrders, p -> true);

            // Setup search filter
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredOrders.setPredicate(order -> {
                    if (newVal == null || newVal.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newVal.toLowerCase();
                    return order.getOrderNumber().toLowerCase().contains(lowerCaseFilter);
                });

                // Handle empty results
                if (filteredOrders.isEmpty()) {
                    noOrdersLabel.setVisible(true);
                    cardContainer.getChildren().clear();
                    pageInfoLabel.setText(" ");
                    paginationBox.getChildren().clear();
                } else {
                    noOrdersLabel.setVisible(false);
                    currentPage = 1;
                    totalPages = (int) Math.ceil((double) filteredOrders.size() / ORDERS_PER_PAGE); // Update pagination
                    updatePagination();
                    showPage(currentPage);
                }
            });
            totalPages = (int) Math.ceil((double) filteredOrders.size() / ORDERS_PER_PAGE); // Initialize pagination normally
            updatePagination();
            showPage(currentPage);
        } catch (Exception e) {
            showError("Could not load the order card: " + e.getMessage());
        }
    }

    private void showPage(int pageNumber) {
        cardContainer.getChildren().clear();

        int start = (pageNumber - 1) * ORDERS_PER_PAGE;
        int end = Math.min(start + ORDERS_PER_PAGE, filteredOrders.size());

        List<Order> pageOrders = filteredOrders.subList(start, end);

        for (Order order : pageOrders) {
            addOrderCard(order);
        }

        pageInfoLabel.setText("Page " + currentPage + " of " + totalPages);
    }

    private void updatePagination() {
        paginationBox.getChildren().clear();

        for (int i = 1; i <= totalPages; i++) {
            ToggleButton btn = new ToggleButton(String.valueOf(i));
            int finalI = i;
            btn.setOnAction(event -> {
                currentPage = finalI;
                showPage(currentPage);
                highlightSelectedPage();
            });
            paginationBox.getChildren().add(btn);
        }

        highlightSelectedPage();
    }

    private void highlightSelectedPage() {
        for (Node node : paginationBox.getChildren()) {
            if (node instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) node;
                btn.setSelected(Integer.parseInt(btn.getText()) == currentPage);
            }
        }
    }

    public void addOrderCard(Order order) {
        try {
            Pair<Parent, OperatorOrderCardController> pair =
                    FXMLManager.INSTANCE.getFXML(FXMLPath.OPERATOR_ORDER_CARD);

            OperatorOrderCardController controller = pair.getValue();
            controller.setOrderData(order, this);

            cardContainer.getChildren().add(pair.getKey());

        } catch (Exception e) {
            showError("Error loading order card for Order Nr.: " + order.getOrderNumber());
        }
    }

    public void openTakePictureView(Order selectedOrder) {
        Navigation.goToCameraView(selectedOrder);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> errorLabel.setVisible(false));
        pause.play();
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    public void OnClickLogoutButton(ActionEvent actionEvent) {
        Navigation.goToTitleScreen();
    }
}
