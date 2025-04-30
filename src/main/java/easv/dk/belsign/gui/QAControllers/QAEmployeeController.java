package easv.dk.belsign.gui.QAControllers;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class QAEmployeeController {
    @FXML
    private VBox cardContainer;
    @FXML
    private Button createOrderButton;

    @FXML
    private void initialize() {
//        // Simulate loading 2 orders
//        for (int i = 0; i < 2; i++) {
//            cardContainer.getChildren().add(createOrderCard("4504202500" + i, "Order Description " + i, "by Operator " + i));
//        }
        createOrderButton.setOnAction(event -> onCreateOrderClick());

    }

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

    public void addNewOrderCard(/* you can pass an Order object later */) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.QA_ORDER_CARD));
            Parent card = loader.load();

           ///

            cardContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    }

