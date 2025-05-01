package easv.dk.belsign;

import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.StageManager;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * =========================================================
 * Exam project: BelSign Photo Documentation
 * June 2025
 * Exam Project Group X: Davide, Jianming, Lucas & Jennifer
 * =========================================================
 * */

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        StageManager stageManager = new StageManager(); // Create the StageManager
        stageManager.setCurrentStage(primaryStage);     // Set the main window

        ViewManager.INSTANCE.setStageManager(stageManager);

        ViewManager.INSTANCE.showStage(FXMLPath.MAIN_LOGIN, "BelSign - Login", false);
    }

    public static void main(String[] args) {

        launch(args);
    }
}
