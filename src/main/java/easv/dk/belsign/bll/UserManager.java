package easv.dk.belsign.bll;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.web.UserDAO;

public class UserManager {
    private final UserDAO userDAO;

    public UserManager() {
        this.userDAO = new UserDAO();

    }

    public User authenticateAndGetUser(String email, String password) {

        return userDAO.authenticateAndGetUser(email, password);

    }

    public User getUserByAccessCode(String accessCode) {

        return userDAO.getUserByAccessCode(accessCode);

    }

}
