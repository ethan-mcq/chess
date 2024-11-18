package service;

import dataaccess.*;
import model.*;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserTest {

    private Data dataSource;
    private UserS userService;
    private UserDao userDao;
    private AuthDao authDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataSource = new Data(DataType.MEM_DATA);
        userService = new UserS(dataSource);
        userDao = dataSource.fetchClientData(UserDao.class);
        authDao = dataSource.fetchClientData(AuthDao.class);
    }

    @Test
    @DisplayName("Create user with new username")
    public void testCreateUserNewUsername() throws DataAccessException {
        UserM newUser = new UserM("newUser", "password123", "newuser@mail.com");
        Auth authResult = userService.createUser(newUser);

        assertNotNull(authResult, "Auth result should not be null");
        assertEquals("newUser", authResult.username(), "Usernames should match");
    }

    @Test
    @DisplayName("Create user with existing username")
    public void testCreateUserExistingUsername() throws DataAccessException {
        UserM existingUser = new UserM("existingUser", "password123", "existinguser@mail.com");
        userService.createUser(existingUser);

        UserM newUserWithSameUsername = new UserM("existingUser", "newpassword", "newuser@mail.com");
        Auth authResult = userService.createUser(newUserWithSameUsername);

        assertNull(authResult, "Auth result should be null for existing username");
    }

    @Test
    @DisplayName("Delete all UserS")
    public void testDeleteAllUserS() throws DataAccessException {
        UserM user1 = new UserM("user1", "password123", "user1@mail.com");
        UserM user2 = new UserM("user2", "password123", "user2@mail.com");

        userService.createUser(user1);
        userService.createUser(user2);

        userService.deleteAll();

        assertNull(userDao.getUser("user1"), "User1 should be null after deletion");
        assertNull(userDao.getUser("user2"), "User2 should be null after deletion");
    }
}