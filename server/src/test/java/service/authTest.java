package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class authTest {

    private data dataSource;
    private authS authService;
    private authDAO authDAO;
    private userDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {

        dataSource = new data(dataTypes.MEM_DATA);
        authService = new authS(dataSource);
        authDAO = dataSource.fetchClientData(authDAO.class);
        userDAO = dataSource.fetchClientData(userDAO.class);
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void testLogin_ValidCredentials() throws DataAccessException {
        String username = "testUser";
        String password = "password";
        String hashedPassword = authS.hashPassword(password);

        user user = new user(username, hashedPassword, "email@mail.com");
        userDAO.insertUser(user);
        authDAO.insertAuth(new auth("validToken", username));

        login loginRequest = new login(username, password);
        auth authResult = authService.login(loginRequest);

        assertNotNull(authResult);
        assertEquals(username, authResult.username());
    }

    @Test
    @DisplayName("Login with invalid credentials")
    public void testLogin_InvalidCredentials() throws DataAccessException {
        String username = "testUser";
        String password = "password";
        String wrongPassword = "wrongPassword";
        String hashedWrongPassword = authS.hashPassword(wrongPassword);

        user user = new user(username, hashedWrongPassword, "email@mail.com");
        userDAO.insertUser(user);

        login loginRequest = new login(username, password);
        auth authResult = authService.login(loginRequest);

        assertNull(authResult);
    }

    @Test
    @DisplayName("Get authentication data with valid token")
    public void testGetAuthData_ValidToken() throws DataAccessException {
        String authToken = "validToken";

        auth auth = new auth(authToken, "testUser");
        authDAO.insertAuth(auth);

        auth authResult = authService.getAuthData(authToken);

        assertNotNull(authResult);
        assertEquals(authToken, authResult.authToken());
    }

    @Test
    @DisplayName("Logout with valid token")
    public void testLogout_ValidToken() throws DataAccessException {
        String authToken = "validToken";

        auth auth = new auth(authToken, "testUser");
        authDAO.insertAuth(auth);

        authService.logout(authToken);

        auth authResult = authDAO.getAuth(authToken);
        assertNull(authResult);
    }

    @Test
    @DisplayName("Delete all authentication tokens")
    public void testDeleteAll() throws DataAccessException {
        authDAO.insertAuth(new auth("token1", "user1"));
        authDAO.insertAuth(new auth("token2", "user2"));

        authService.deleteAll();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }
}