package service;

import dataaccess.authDAO;
import dataaccess.data;
import dataaccess.userDAO;
import dataaccess.DataAccessException;
import model.auth;
import model.login;
import model.user;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;

public class authTest {

    private data mockData;
    private authS authService;
    private authDAO mockAuthDAO;
    private userDAO mockUserDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        mockData = mock(data.class);
        authService = new authS(mockData);
        mockAuthDAO = mock(authDAO.class);
        mockUserDAO = mock(userDAO.class);

        when(mockData.fetchClientData(authDAO.class)).thenReturn(mockAuthDAO);
        when(mockData.fetchClientData(userDAO.class)).thenReturn(mockUserDAO);
    }

    @Test
    @DisplayName("Login with valid credentials")
    public void testLogin_ValidCredentials() throws Exception {
        String username = "testUser";
        String password = "password";
        String hashedPassword = authS.hashPassword(password);

        user mockUser = new user(username, hashedPassword, "email@mail.com");

        when(mockUserDAO.getUser(username)).thenReturn(mockUser);
        when(mockAuthDAO.insertAuth(any(auth.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        login loginRequest = new login(username, password);
        auth authResult = authService.login(loginRequest);

        assertNotNull(authResult);
        assertEquals(username, authResult.username());
    }

    @Test
    @DisplayName("Login with invalid credentials")
    public void testLogin_InvalidCredentials() throws Exception {
        String username = "testUser";
        String password = "password";
        String wrongPassword = "wrongPassword";
        String hashedPassword = authS.hashPassword(wrongPassword);

        user mockUser = new user(username, hashedPassword, "email@mail.com");

        when(mockUserDAO.getUser(username)).thenReturn(mockUser);

        login loginRequest = new login(username, password);
        auth authResult = authService.login(loginRequest);

        assertNull(authResult);
    }

    @Test
    @DisplayName("Get authentication data with valid token")
    public void testGetAuthData_ValidToken() throws Exception {
        String authToken = "validToken";

        auth mockAuth = new auth(authToken, "testUser");

        when(mockAuthDAO.getAuth(authToken)).thenReturn(mockAuth);

        auth authResult = authService.getAuthData(authToken);

        assertNotNull(authResult);
        assertEquals(authToken, authResult.authToken());
    }

    @Test
    @DisplayName("Logout with valid token")
    public void testLogout_ValidToken() throws Exception {
        String authToken = "validToken";

        // Use doNothing().when for void methods and handle exceptions if any
        doAnswer(invocation -> null).when(mockAuthDAO).removeAuth(authToken);

        authService.logout(authToken);

        // Verify that the removeAuth method was called once with the correct token
        verify(mockAuthDAO, times(1)).removeAuth(authToken);
    }

    @Test
    @DisplayName("Delete all authentication tokens")
    public void testDeleteAll() throws Exception {
        // Use doNothing().when for void methods and handle exceptions if any
        doAnswer(invocation -> null).when(mockAuthDAO).removeAuth();

        authService.deleteAll();

        // Verify that the removeAuth method was called once
        verify(mockAuthDAO, times(1)).removeAuth();
    }
}