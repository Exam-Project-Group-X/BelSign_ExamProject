package easv.dk.belsign.utils;

import easv.dk.belsign.bll.util.PasswordUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {
    @Test
    void hashProducesDifferentValueThanPlainText() {
        String pwd = "BelMan3t!";
        String hash = PasswordUtils.hashPassword(pwd);
        assertNotNull(hash, "HashCode should not be null");
        assertNotEquals(pwd, hash,
                "HashCode must not equal the plain‐text password");
    }

    @Test
    void verifyAcceptsCorrectPassword() {
        String pwd = "Another$Pass123";
        String hash = PasswordUtils.hashPassword(pwd);
        assertTrue(PasswordUtils.checkPassword(pwd, hash),
                "verify should return true for the original password");
    }

    @Test
    void verifyRejectsWrongPassword() {
        String pwd = "CorrectPasswordSample";
        String hash = PasswordUtils.hashPassword(pwd);
        String badP = "correctpasswordsample";  // different case
        assertFalse(PasswordUtils.checkPassword(badP, hash),
                "verify should return false for an incorrect password");
    }
    @Test
    void hashIsNonDeterministic() {
        String pwd = "RepeatMe!";
        String h1 = PasswordUtils.hashPassword(pwd);
        String h2 = PasswordUtils.hashPassword(pwd);
        assertNotEquals(h1, h2,
                "Each call to hash should produce a different salt, " +
                        "hence a different hash");
        assertTrue(PasswordUtils.checkPassword(pwd, h1));
        assertTrue(PasswordUtils.checkPassword(pwd, h2));
    }
}