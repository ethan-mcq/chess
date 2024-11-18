package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class userTest {

    private data dataSource;
    private userS userService;
    private userDAO userDao;
    private authDAO authDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataSource = new data(dataTypes.MEM_DATA);
        userService = new userS(dataSource);
        userDao = dataSource.fetchClientData(userDAO.class);
        authDao = dataSource.fetchClientData(authDAO.class);
    }

    @Test
    @DisplayName("Create user with new username")
    public void testCreateUser_NewUsername() throws DataAccessException {
        user newUser = new user("newUser", "password123", "newuser@mail.com");
        auth authResult = userService.createUser(newUser);

        assertNotNull(authResult, "Auth result should not be null");
        assertEquals("newUser", authResult.username(), "Usernames should match");
    }

    @Test
    @DisplayName("Create user with existing username")
    public void testCreateUser_ExistingUsername() throws DataAccessException {
        user existingUser = new user("existingUser", "password123", "existinguser@mail.com");
        userService.createUser(existingUser);

        user newUserWithSameUsername = new user("existingUser", "newpassword", "newuser@mail.com");
        auth authResult = userService.createUser(newUserWithSameUsername);

        assertNull(authResult, "Auth result should be null for existing username");
    }

    @Test
    @DisplayName("Delete all users")
    public void testDeleteAllUsers() throws DataAccessException {
        user user1 = new user("user1", "password123", "user1@mail.com");
        user user2 = new user("user2", "password123", "user2@mail.com");

        userService.createUser(user1);
        userService.createUser(user2);

        userService.deleteAll();

        assertNull(userDao.getUser("user1"), "User1 should be null after deletion");
        assertNull(userDao.getUser("user2"), "User2 should be null after deletion");
    }
}