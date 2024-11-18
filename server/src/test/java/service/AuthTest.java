package src.test.java.dataaccess.service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    private Data DataSource;
    private AuthS AuthService;
    private AuthDao AuthDao;
    private UserDao UserDao;

    @BeforeEach
    public void setUp() throws DataAccessException {

        DataSource = new Data(DataType.MEM_DATA);
        AuthService = new AuthS(DataSource);
        AuthDao = DataSource.fetchClientData(AuthDao.class);
        UserDao = DataSource.fetchClientData(UserDao.class);
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void testLogin_ValidCredentials() throws DataAccessException {
        String username = "testUser";
        String password = "password";
        String hashedPassword = AuthS.hashPassword(password);

        UserM user = new UserM(username, hashedPassword, "email@mail.com");
        UserDao.insertUser(user);
        AuthDao.insertAuth(new Auth("validToken", username));

        Login loginRequest = new Login(username, password);
        Auth authResult = AuthService.login(loginRequest);

        assertNotNull(authResult);
        assertEquals(username, authResult.username());
    }

    @Test
    @DisplayName("Login with invalid credentials")
    public void testLogin_InvalidCredentials() throws DataAccessException {
        String username = "testUser";
        String password = "password";
        String wrongPassword = "wrongPassword";
        String hashedWrongPassword = AuthS.hashPassword(wrongPassword);

        UserM user = new UserM(username, hashedWrongPassword, "email@mail.com");
        UserDao.insertUser(user);

        Login loginRequest = new Login(username, password);
        Auth authResult = AuthService.login(loginRequest);

        assertNull(authResult);
    }

    @Test
    @DisplayName("Get authentication Data with valid token")
    public void testGetAuthData_ValidToken() throws DataAccessException {
        String authToken = "validToken";

        Auth auth = new Auth(authToken, "testUser");
        AuthDao.insertAuth(auth);

        Auth authResult = AuthService.getAuthData(authToken);

        assertNotNull(authResult);
        assertEquals(authToken, authResult.authToken());
    }

    @Test
    @DisplayName("Logout with valid token")
    public void testLogout_ValidToken() throws DataAccessException {
        String authToken = "validToken";

        Auth auth = new Auth(authToken, "testUser");
        AuthDao.insertAuth(auth);

        AuthService.logout(authToken);

        Auth authResult = AuthDao.getAuth(authToken);
        assertNull(authResult);
    }

    @Test
    @DisplayName("Delete all authentication tokens")
    public void testDeleteAll() throws DataAccessException {
        AuthDao.insertAuth(new Auth("token1", "user1"));
        AuthDao.insertAuth(new Auth("token2", "user2"));

        AuthService.deleteAll();

        assertNull(AuthDao.getAuth("token1"));
        assertNull(AuthDao.getAuth("token2"));
    }
}