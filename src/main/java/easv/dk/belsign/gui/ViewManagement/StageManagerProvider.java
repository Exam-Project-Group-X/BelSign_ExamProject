package easv.dk.belsign.gui.ViewManagement;

public class StageManagerProvider {

    private static StageManager instance;

    public static void init(StageManager stageManager) {
        instance = stageManager;

        }
    public static StageManager get() {
        if (instance == null) {
            throw new IllegalStateException("StageManager has not been initialized!");
        }
        return instance;
    }
}

