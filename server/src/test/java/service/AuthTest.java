package src.test.java.service;

import dataaccess.*;
import service.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    private Data dataSource;
    private AuthS authService;
    private AuthDao authDao;
    private UserDao userDao;

    @BeforeEach
    public void setUp() throws DataAccessException {

        dataSource = new Data(DataType.MEM_DATA);
        authService = new AuthS(dataSource);
        authDao = dataSource.fetchClientData(AuthDao.class);
        userDao = dataSource.fetchClientData(UserDao.class);
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void testLoginValidCredentials() throws DataAccessException {
        String username = "testUser";
        String password = "password";
        String hashedPassword = AuthS.hashPassword(password);

        UserM user = new UserM(username, hashedPassword, "email@mail.com");
        userDao.insertUser(user);
        authDao.insertAuth(new Auth("validToken", username));

        Login loginRequest = new Login(username, password);
        Auth authResult = authService.login(loginRequest);

        assertNotNull(authResult);
        assertEquals(username, authResult.username());
    }

    @Test
    @DisplayName("Login with invalid credentials")
    public void testLoginInvalidCredentials() throws DataAccessException {
        String username = "testUser";
        String password = "password";
        String wrongPassword = "wrongPassword";
        String hashedWrongPassword = AuthS.hashPassword(wrongPassword);

        UserM user = new UserM(username, hashedWrongPassword, "email@mail.com");
        userDao.insertUser(user);

        Login loginRequest = new Login(username, password);
        Auth authResult = authService.login(loginRequest);

        assertNull(authResult);
    }

    @Test
    @DisplayName("Get authentication Data with valid token")
    public void testGetAuthDataValidToken() throws DataAccessException {
        String authToken = "validToken";

        Auth auth = new Auth(authToken, "testUser");
        authDao.insertAuth(auth);

        Auth authResult = authService.getAuthData(authToken);

        assertNotNull(authResult);
        assertEquals(authToken, authResult.authToken());
    }

    @Test
    @DisplayName("Logout with valid token")
    public void testLogoutValidToken() throws DataAccessException {
        String authToken = "validToken";

        Auth auth = new Auth(authToken, "testUser");
        authDao.insertAuth(auth);

        authService.logout(authToken);

        Auth authResult = authDao.getAuth(authToken);
        assertNull(authResult);
    }

    @Test
    @DisplayName("Delete all authentication tokens")
    public void testDeleteAll() throws DataAccessException {
        authDao.insertAuth(new Auth("token1", "user1"));
        authDao.insertAuth(new Auth("token2", "user2"));

        authService.deleteAll();

        assertNull(authDao.getAuth("token1"));
        assertNull(authDao.getAuth("token2"));
    }
}