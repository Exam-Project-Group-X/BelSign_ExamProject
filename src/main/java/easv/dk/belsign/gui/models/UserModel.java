package easv.dk.belsign.gui.models;


import easv.dk.belsign.be.User;
import easv.dk.belsign.bll.UserManager;


public class UserModel {

    private final UserManager userManager = new UserManager();

    private User loggedInUser;

    public User authenticate(String email, String password) {
        this.loggedInUser = userManager.authenticateAndGetUser(email, password);
        return loggedInUser;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getUserByAccessCode(String accessCode) {
        this.loggedInUser = userManager.getUserByAccessCode(accessCode);
        return loggedInUser;
    }


}
