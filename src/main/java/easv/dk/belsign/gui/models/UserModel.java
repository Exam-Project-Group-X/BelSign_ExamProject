package easv.dk.belsign.gui.models;
import easv.dk.belsign.be.User;
import easv.dk.belsign.bll.UserManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
public class UserModel {

    private final UserManager userManager = new UserManager();
    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final ObservableList<String> allRoleNames = FXCollections.observableArrayList();
    private User loggedInUser;

    public User authenticate(String email, String password) throws SQLException {
        this.loggedInUser = userManager.authenticateAndGetUser(email, password);
        return loggedInUser;
    }

    public ObservableList<String> getAllRoleNames() throws SQLException {
        allRoleNames.setAll(userManager.getAllRoles());
        return allRoleNames;
    }

    public ObservableList<User> getAllUsers() throws SQLException {allUsers.setAll(userManager.getAllUsers());
        return allUsers;
    }

    public User createNewUser(User user) throws SQLException {
        User newUser = userManager.createNewUser(user);
        allUsers.add(newUser);
        return newUser;
    }

    public void deleteUser(User user) throws SQLException {
        userManager.deleteUser(user);
        allUsers.remove(user);
    }
    public void updateUser(User user) throws SQLException {
        userManager.updateUser(user);
        int index = allUsers.indexOf(user);
        if (index != -1) {
            allUsers.set(index, user);
        }
}

}