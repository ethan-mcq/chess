package src.test.java.dataaccess.service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserTest {

    private Data DataSource;
    private UserS UserService;
    private UserDao UserDao;
    private AuthDao AuthDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DataSource = new Data(DataType.MEM_DATA);
        UserService = new UserS(DataSource);
        UserDao = DataSource.fetchClientData(UserDao.class);
        AuthDao = DataSource.fetchClientData(AuthDao.class);
    }

    @Test
    @DisplayName("Create user with new username")
    public void testCreateUser_NewUsername() throws DataAccessException {
        UserM newUser = new UserM("newUser", "password123", "newuser@mail.com");
        Auth authResult = UserService.createUser(newUser);

        assertNotNull(authResult, "Auth result should not be null");
        assertEquals("newUser", authResult.username(), "Usernames should match");
    }

    @Test
    @DisplayName("Create user with existing username")
    public void testCreateUser_ExistingUsername() throws DataAccessException {
        UserM existingUser = new UserM("existingUser", "password123", "existinguser@mail.com");
        UserService.createUser(existingUser);

        UserM newUserWithSameUsername = new UserM("existingUser", "newpassword", "newuser@mail.com");
        Auth authResult = UserService.createUser(newUserWithSameUsername);

        assertNull(authResult, "Auth result should be null for existing username");
    }

    @Test
    @DisplayName("Delete all UserS")
    public void testDeleteAllUserS() throws DataAccessException {
        UserM user1 = new UserM("user1", "password123", "user1@mail.com");
        UserM user2 = new UserM("user2", "password123", "user2@mail.com");

        UserService.createUser(user1);
        UserService.createUser(user2);

        UserService.deleteAll();

        assertNull(UserDao.getUser("user1"), "User1 should be null after deletion");
        assertNull(UserDao.getUser("user2"), "User2 should be null after deletion");
    }
}