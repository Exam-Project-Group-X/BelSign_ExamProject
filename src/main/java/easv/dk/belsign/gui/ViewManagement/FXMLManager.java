package easv.dk.belsign.gui.ViewManagement;


import easv.dk.belsign.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;


public enum FXMLManager {

    INSTANCE;

    public <T> Pair<Parent, T> getFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getFXMLPath(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();
            return new Pair<>(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    private static URL getFXMLPath(String fxmlPath) {
        URL resource = Main.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("FXML file not found: " + fxmlPath);
        }
        return resource;
    }
}
