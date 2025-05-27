package easv.dk.belsign.dal;

import easv.dk.belsign.be.User;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    // Define the methods that will be implemented in the UserDAO class
    List<User> getAllUsers() throws SQLException;

    User createNewUser(User user) throws SQLException;
    void deleteUser(User user) throws SQLException;
    void updateUser(User user) throws SQLException;
    List<String> getAllRoleNames() throws SQLException;
    User authenticateAndGetUser(String email, String password) throws SQLException;
}
