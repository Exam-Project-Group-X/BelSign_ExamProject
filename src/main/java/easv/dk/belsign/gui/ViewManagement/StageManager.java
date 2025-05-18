package easv.dk.belsign.gui.ViewManagement;

import easv.dk.belsign.exceptions.ViewException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class StageManager {

    private final SceneManager sceneManager;
    private final StageSettings stageSettings;
    private final Map<String, Stage> stageCache = new HashMap<>();

    private final FXMLManager fxmlManager = FXMLManager.INSTANCE;

    private static class StageSettings {
        protected Stage currentStage = null;
        protected final boolean isResizable = false;
        protected String title = "";
    }

    public StageManager() {
        stageSettings = new StageSettings();
        sceneManager = new SceneManager(stageSettings.currentStage);
    }

    public void setCurrentStage(Stage stage) {
        stageSettings.currentStage = stage;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void loadStage(String fxmlFile, String title, boolean isModal) {
        if (!stageCache.containsKey(fxmlFile)) {
            Parent root = fxmlManager.getFXML(fxmlFile).getKey();
            Stage stage = new Stage();
            stage.setResizable(stageSettings.isResizable);
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(isModal ? Modality.APPLICATION_MODAL : Modality.NONE);

            /* ---------- centre once the window is actually shown ---------- */
            stage.setOnShown(e -> stage.centerOnScreen());
            /* ---------------------------------------------------------------- */

            stageCache.put(fxmlFile, stage);
        }
    }

    public Stage getStage(String fxmlFile) throws ViewException {
        if (stageCache.get(fxmlFile) == null) {
            throw new ViewException("Stage not loaded: " + fxmlFile);
        }
        sceneManager.setCurrentStage(stageCache.get(fxmlFile));
        return stageCache.get(fxmlFile);
    }

    public void showStage(String fxmlFile) {
        if (stageSettings.currentStage != null) {
            hideCurrentStage();
        }

        try {
            stageSettings.currentStage = getStage(fxmlFile);
            stageSettings.currentStage.show();
        } catch (ViewException e) {
            e.printStackTrace();
        }
    }

    public void hideCurrentStage() {
        if (stageSettings.currentStage != null) {
            stageSettings.currentStage.hide();
        }
    }

    public void closeStage(String fxmlFile) {
        Stage stage = stageCache.remove(fxmlFile);
        if (stage != null) {
            stage.close();
        }
    }

    public void showPopup(String fxmlFile, String title) {
        if (!stageCache.containsKey(fxmlFile)) {
            loadStage(fxmlFile, title, true);
        }
        try {
            getStage(fxmlFile).show();
        } catch (ViewException e) {
            System.out.println(e.getMessage());
        }
    }

    public void hidePopup(String fxmlFile) {
        try {
            getStage(fxmlFile).hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchScene(Parent root) {
        stageSettings.currentStage.setScene(new Scene(root));
        stageSettings.currentStage.sizeToScene();
        stageSettings.currentStage.centerOnScreen();
    }

}
