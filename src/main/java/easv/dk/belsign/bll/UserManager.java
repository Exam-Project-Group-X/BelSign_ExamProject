package easv.dk.belsign.bll;

import easv.dk.belsign.be.User;
import easv.dk.belsign.bll.util.UserFilter;
import easv.dk.belsign.dal.IUserDAO;
import easv.dk.belsign.dal.web.UserDAO;

import java.sql.SQLException;
import java.util.List;

import javafx.collections.ObservableList;

public class UserManager {
    private IUserDAO userDAO;
    private UserFilter filter = new UserFilter();

    public UserManager() {
        this(new UserDAO());                 // uses the real DAO
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public User createNewUser(User user) throws SQLException {
        return userDAO.createNewUser(user); // this returns the newly created UserID from the DB
    }

    public void deleteUser(User user) throws SQLException {
        userDAO.deleteUser(user);
    }

    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }

    public User authenticateAndGetUser(String email, String password) throws SQLException {
        return userDAO.authenticateAndGetUser(email, password);
    }

    public List<String> getAllRoles() throws SQLException {
        return userDAO.getAllRoleNames();
    }

    public UserManager(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> filterUsers(List<User> allUsers, String search, String role) {
        return filter.filter(allUsers, search, role);
    }
}
