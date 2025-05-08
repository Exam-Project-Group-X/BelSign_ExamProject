package easv.dk.belsign.bll;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.IUserDAO;
import easv.dk.belsign.dal.web.UserDAO;

import java.sql.SQLException;
import java.util.List;

public class UserManager {
    private final IUserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public void createNewUser(User user) throws SQLException {
        userDAO.createNewUser(user);
    }

    public void deleteUser(User user) throws SQLException {
        userDAO.deleteUser(user);
    }

    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }

}
