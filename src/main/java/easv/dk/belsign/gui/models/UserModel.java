package easv.dk.belsign.gui.models;
import easv.dk.belsign.be.User;
import easv.dk.belsign.bll.UserManager;
import easv.dk.belsign.bll.util.PasswordUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.List;

public class UserModel {

    private final UserManager userManager = new UserManager();
    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final ObservableList<String> allRoleNames = FXCollections.observableArrayList();
    private final ObservableList<User> displayedUsers = FXCollections.observableArrayList();
    private User loggedInUser;

    public User authenticate(String email, String rawPassword) throws SQLException {
        // ensure we have the latest list
        getAllUsers();
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)
                    && PasswordUtils.checkPassword(rawPassword, u.getPasswordHash())) {
                loggedInUser = u;
                return u;
            }
        }
        return null;
    }

    public ObservableList<String> getAllRoleNames() throws SQLException {
        allRoleNames.setAll(userManager.getAllRoles());
        return allRoleNames;
    }

    public ObservableList<User> getAllUsers() throws SQLException {
        allUsers.setAll(userManager.getAllUsers());
        return allUsers;
    }

    public User createNewUser(User user) throws SQLException {
        String hashed = PasswordUtils.hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashed);

        // 2) save and update local list
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

    public ObservableList<User> getDisplayedUsers() {
        return displayedUsers;
    }

    public void filterUsers(String search, String role) {
        List<User> filterResults = userManager.filterUsers(allUsers, search, role);
        displayedUsers.setAll(filterResults);
    }

}