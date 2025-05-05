package easv.dk.belsign;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.StageManager;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;


public class TestOperator extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        StageManager stageManager = new StageManager(); // Create the StageManager
        stageManager.setCurrentStage(primaryStage);     // Set the main window

        ViewManager.INSTANCE.setStageManager(stageManager);

        ViewManager.INSTANCE.showStage(FXMLPath.OPERATOR_DASHBOARD, "OPERATOR_DASHBOARD", false);
        primaryStage.setOnShown(e -> primaryStage.centerOnScreen());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
