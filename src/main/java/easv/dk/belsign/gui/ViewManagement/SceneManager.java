package easv.dk.belsign.gui.ViewManagement;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.swing.border.Border;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private Stage currentStage;
    private BorderPane stageRoot;
    private final Map<String, Scene> sceneCache = new HashMap<>();

    public SceneManager(){
        this.currentStage = null;
        this.stageRoot = null;
    }

    public SceneManager(Stage currentStage){
        this.currentStage = currentStage;
    }

    public void setCurrentStage(Stage stage){
        this.currentStage = stage;
    }

    public Stage getCurrentStage() {
        return this.currentStage;
    }

    public void setStageRoot(BorderPane stageRoot)
    {
        this.stageRoot = stageRoot;
    }

    public Scene loadScene(String fxmlPath) {
        Pair<Parent, Object> fxml = FXMLManager.INSTANCE.getFXML(fxmlPath);
        if (fxml != null) {
            return new Scene(fxml.getKey()); // ✅ fresh root every time
        }
        return null;
    }

    public void switchScene(String fxmlPath){
        if(currentStage != null){
            currentStage.setScene(loadScene(fxmlPath));
            currentStage.sizeToScene();        // resize to the new root’s pref size
            currentStage.centerOnScreen();     // keeps the window centred
        }
    }

    public void switchDashboard(String fxmlPath, String title)
    {
        if(currentStage != null){
            if(stageRoot != null){
                stageRoot.setCenter(loadScene(fxmlPath).getRoot());
                currentStage.setTitle(title);
            }
        }
    }
}
