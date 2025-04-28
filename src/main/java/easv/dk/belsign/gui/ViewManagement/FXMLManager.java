package easv.dk.belsign.gui.ViewManagement;


import easv.dk.belsign.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum FXMLManager {

    INSTANCE;
    private final Map<String, Pair<Parent, Object>> loadedFXMLs = new HashMap<>();
    private final Map<String, Class<?>> classMap = new LinkedHashMap<>();

    FXMLManager() {}

    // Loads FXML document and put it into the cache memory, returns a pair of a root element and controller of the document
    public <T> Pair<Parent, T> loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getFXMLPath(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();
            Pair<Parent, Object> parentFXMLControllerPair = new Pair<>(root, controller);
            loadedFXMLs.put(fxmlPath, parentFXMLControllerPair);
            return new Pair<>(parentFXMLControllerPair.getKey(), (T) parentFXMLControllerPair.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }

    }


    // Gets fxml document from the cache, if fxml is not in the cache load document
    public <T> Pair<Parent, T> getFXML(String fxmlPath) {
        if(!loadedFXMLs.containsKey(fxmlPath)){
            return loadFXML(fxmlPath);
//      throw new RuntimeException("FXML wasn't loaded: " + fxmlPath);
        }

        Pair<Parent, Object> fxmlParentControllerPair = loadedFXMLs.get(fxmlPath);
        return new Pair<>(fxmlParentControllerPair.getKey(), (T) fxmlParentControllerPair.getValue());
    }


    // returns FXML document path
    private static URL getFXMLPath(String fxmlPath) {
        URL resource = Main.class.getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("FXML file not found: " + fxmlPath);
        }
        return resource;
    }


}
