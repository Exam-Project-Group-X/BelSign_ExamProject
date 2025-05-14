package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QAEmployeeController implements Initializable {
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
    private Button createOrderButton;
    @FXML
    private Button logoutButton;

    private static final int PAGE_SIZE = 3;
    private static final int TOGGLE_COUNT = 5;// cards per page
    private int currentPage = 1;
    private int pageCount  = 1;
    private List<Order> orders;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private final QAEmployeeModel qamodel = new QAEmployeeModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Get all orders from the QAEmployeeModel
            orders = qamodel.getAllOrders();
            pageCount = (int)Math.ceil((double)orders.size() / PAGE_SIZE);
            loadPage(currentPage);
            updatePaginationToggles();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.QA_ORDER_CARD));
            Parent card = loader.load();

            ///

            OrderCardController controller = loader.getController();
            controller.setOrderData(order);

            cardContainer.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void onClickLogoutBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
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


