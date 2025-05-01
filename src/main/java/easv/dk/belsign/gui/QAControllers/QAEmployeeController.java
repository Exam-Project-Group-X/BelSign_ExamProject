package easv.dk.belsign.gui.QAControllers;

import easv.dk.belsign.be.Order;
import easv.dk.belsign.gui.QAControllers.NewOrderDialogController;
import easv.dk.belsign.gui.QAControllers.components.QAOrderCardController;
import easv.dk.belsign.gui.QAControllers.model.QAmodel;
import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class QAEmployeeController {
    @FXML
    private VBox cardContainer;
    @FXML
    private Button createOrderButton;

    private final QAmodel model = new QAmodel();
    @FXML
    private void initialize() {

        try {
            // Get all orders from the QAmodel
            for (Order order : model.getAllOrders()) {
                addNewOrderCard(order); // Add each order card to the UI
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        createOrderButton.setOnAction(event -> onCreateOrderClick());

    }
/// Generate QC Report button only clickable after approving ALL photos (i.e. Status "Complete"
///  -> Then you can Generate QC Report)


    private void onCreateOrderClick() {
//        ViewManager.INSTANCE.showStage(FXMLPath.NEW_ORDER_DIALOG, "Create New Order", true);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.NEW_ORDER_DIALOG));
            Parent root = loader.load();

            NewOrderDialogController controller = loader.getController();
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

            QAOrderCardController controller = loader.getController();
            controller.setOrderData(order); // ✅ Pass real order data to UI

            cardContainer.getChildren().add(card);
        } catch (IOException e) {
            e.printStackTrace();

        }


    }
}


