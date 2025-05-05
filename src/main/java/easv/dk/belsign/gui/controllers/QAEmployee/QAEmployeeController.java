package easv.dk.belsign.gui.controllers.QAEmployee;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.models.QAEmployeeModel;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class QAEmployeeController {
    @FXML
    private Label pageInfoLabel;
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

    private static final int PAGE_SIZE = 3;                    // cards per page
    private int currentPage = 1;
    private int pageCount  = 1;
    private List<Order> orders;

    private final QAEmployeeModel model = new QAEmployeeModel();
    @FXML
    public void initialize() {
        try {
            // Get all orders from the QAEmployeeModel
            orders = model.getAllOrders();
            pageCount = (int)Math.ceil((double)orders.size() / PAGE_SIZE);
            loadPage(currentPage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        createOrderButton.setOnAction(event -> onCreateOrderClick());

        prevPageBtn.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                loadPage(currentPage);
            }
        });

        nextPageBtn.setOnAction(event -> {
            if (currentPage < pageCount) {
                currentPage++;
                loadPage(currentPage);
            }
        });
    }
    /*private void initialize() {
        try {
            // Get all orders from the QAEmployeeModel
            for (Order order : model.getAllOrders()) {
                addNewOrderCard(order); // Add each order card to the UI
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        createOrderButton.setOnAction(event -> onCreateOrderClick());

    }*/
/// Generate QC Report button only clickable after approving ALL photos (i.e. Status "Complete"
///  -> Then you can Generate QC Report)
    public void loadPage(int page) {
        cardContainer.getChildren().clear();
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, orders.size());
        for (int i = start; i < end; i++) {
            addNewOrderCard(orders.get(i));
        }
        pageInfoLabel.setText("Showing page " + currentPage + " of " + pageCount);
    }

    private void onCreateOrderClick() {
//        ViewManager.INSTANCE.showStage(FXMLPath.NEW_ORDER_DIALOG, "Create New Order", true);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.NEW_ORDER_DIALOG));
            Parent root = loader.load();

            CreateNewOrderController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Create New Order");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addNewOrderCard(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.QA_ORDER_CARD));
            Parent card = loader.load();

            ///

            OrderCardController controller = loader.getController();
            controller.setOrderData(order); // ✅ Pass real order data to UI

            cardContainer.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickLogoutBtn(ActionEvent actionEvent) {
        ViewManager.INSTANCE.showScene(FXMLPath.LOGIN);
    }
}


