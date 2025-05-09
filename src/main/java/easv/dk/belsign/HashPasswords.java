package easv.dk.belsign;



import easv.dk.belsign.gui.ViewManagement.FXMLPath;
import easv.dk.belsign.gui.ViewManagement.StageManager;
import easv.dk.belsign.gui.ViewManagement.ViewManager;
import easv.dk.belsign.utils.PasswordUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class HashPasswords extends Application {

    @Override
    public void start(Stage primaryStage) {

        String hashedPassword = PasswordUtils.hashPassword("operator");
        System.out.println(hashedPassword);
    }

    public static void main(String[] args) {

        launch(args);
    }

}
