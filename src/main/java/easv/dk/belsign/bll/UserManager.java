package easv.dk.belsign.bll;
import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.web.UserDAO;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.ObservableList;

public class UserManager {
    private final UserDAO userDAO;
    public UserManager() {
        this.userDAO = new UserDAO();
    }
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }
    public int createNewUser(User user) throws SQLException {
        return userDAO.createNewUser(user); // this returns the newly created UserID from the DB
    }

    public void deleteUser(User user) throws SQLException {
        userDAO.deleteUser(user);
    }
    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }
    public User authenticateAndGetUser(String email, String password) {
        return userDAO.authenticateAndGetUser(email, password);
    }

    public ObservableList<String> getAllRoles() throws SQLException {

        return userDAO.getAllRoleNames();

    }

}
