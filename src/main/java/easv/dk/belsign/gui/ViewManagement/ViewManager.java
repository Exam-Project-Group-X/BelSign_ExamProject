package easv.dk.belsign.gui.ViewManagement;


import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;


    public enum ViewManager {
        INSTANCE;

        private StageManager stageManager;
        private SceneManager sceneManager;
        private final FXMLManager fxmlManager = FXMLManager.INSTANCE;

        ViewManager(){
            stageManager = null;
            sceneManager = null;
        }

        public void setStageManager(StageManager stageManager)
        {
            this.stageManager = stageManager;
            this.sceneManager = this.stageManager.getSceneManager();
        }

        public void showStage(String fxml, String title, boolean isModal){
            if(stageManager != null){
                stageManager.loadStage(fxml, title, isModal);
                stageManager.showStage(fxml);

//      stageManager.setCurrentStage(stageManager);
            }
        }

        public void showScene(String fxml){
            if(stageManager != null){
                sceneManager.switchScene(fxml);
            }
        }

        public SceneManager getSceneManager() {
            return this.sceneManager;
        }

        public void switchDashboard(String fxml, String title){
            if(stageManager != null){
                sceneManager.switchDashboard(fxml, title);
            }
        }

        public void showError(String title, String message) {


            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        public void showPopup(String fxmlFile, String title){
            stageManager.showPopup(fxmlFile, title);
        }

        public void setStageRoot(BorderPane borderPane)
        {
            if(stageManager != null){
                sceneManager.setStageRoot(borderPane);
            }
        }

        public void hidePopup(String fxml){
            if(stageManager != null){
                stageManager.hidePopup(fxml);
            }
        }
    }

