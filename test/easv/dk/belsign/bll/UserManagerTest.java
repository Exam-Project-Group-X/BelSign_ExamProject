package easv.dk.belsign.bll;

import easv.dk.belsign.be.User;
import easv.dk.belsign.dal.IUserDAO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserManagerTest {

    @Test
    void createNewUser() throws Exception {           //  ← add “throws Exception”
        // Arrange
        IUserDAO fakeDao = mock(IUserDAO.class);
        UserManager manager = new UserManager(fakeDao);

        User input = new User(0, "hash", "AA123", "Alice",
                "alice@demo.com", 2, null, null, true, "Admin");
        User saved = new User(7, input.getPasswordHash(), input.getAccessCode(),
                input.getFullName(), input.getEmail(),
                input.getRoleId(), null, null, true, "Admin");

        when(fakeDao.createNewUser(input)).thenReturn(saved);

        // Act
        User result = manager.createNewUser(input);

        // Assert
        assertEquals(7, result.getUserID());
        verify(fakeDao).createNewUser(input);
    }
}