1st Year Exam Project

Working alongside Belman A/S, this is a data-access layered JavaFX Photo Documenting application that is to be integrated, or be a part of, Belman's Production Management system, BelSign.

(application image/screenshot here)

Davide, Jianming, Lucas and Jennifer, June 2025

📚 Adding FXML Files and View Management (SceneManagement)

This project uses a ViewManager system to easily open, switch, and manage different FXML views inside the program.

🎯 Simple Example: Login ➔ Operator Orders
1. Place the FXML files

Both files must be inside:
src/main/resources/easv/dk/belsign/views/

2. Add paths to FXMLPath.java

Open the file FXMLPath.java and add:

public static final String LOGIN = "/easv/dk/belsign/views/LoginView.fxml";
public static final String OPERATOR_ORDERS = "/easv/dk/belsign/views/OperatorOrders.fxml";

3. Load the Login screen first

In your Main.java:

@Override
public void start(Stage stage) {
StageManager stageManager = new StageManager();
stageManager.setCurrentStage(stage);
ViewManager.INSTANCE.setStageManager(stageManager);

    ViewManager.INSTANCE.showStage(FXMLPath.LOGIN, "BelSign - Login", false);
}



4. Switch to Operator Orders after Login

Inside your LoginController (after the user successfully logs in), use this code:

ViewManager.INSTANCE.showScene(FXMLPath.OPERATOR_ORDERS);

✅ This replaces the Login screen with the Operator Orders screen.

📖 General Commands

Open a new scene -> ViewManager.INSTANCE.showScene(FXMLPath.XYZ);
Open a popup window	-> ViewManager.INSTANCE.showStage(FXMLPath.XYZ, "Popup Title", true);
Replace content inside a dashboard -> ViewManager.INSTANCE.switchDashboard(FXMLPath.XYZ, "New Title");
Hide a popup -> ViewManager.INSTANCE.hidePopup(FXMLPath.XYZ);
Close and delete a popup -> ViewManager.INSTANCE.closeStage(FXMLPath.XYZ);

📋 Summary

✅ Always put your .fxml files inside src/main/resources/easv/dk/belsign/views/

✅ Always add a new public static final String in FXMLPath.java when you create a new screen.

✅ Always use ViewManager.INSTANCE.showScene(...) or showStage(...) to change screens.

✅ NEVER use FXMLLoader.load(...) directly yourself.
The system does it for you behind the scenes.

✅ If you see an error like "FXML not found", check if the path is correct inside FXMLPath.java.

🧩 What Each Manager Does
FXMLManager   ---------- Loads and remembers the FXML + Controller
SceneManager  ---------- Switches what you see inside the main window
StageManager  ---------- Creates popup windows
ViewManager   ---------- You use this one! It calls the other managers for you.