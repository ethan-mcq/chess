package service;

import dataaccess.DataAccessException;
import dataaccess.authDAO;
import dataaccess.data;
import dataaccess.userDAO;
import model.auth;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;


public class userTest {

    private data mockData;
    private userDAO mockUserDAO;
    private authDAO mockAuthDAO;
    private userS userService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        mockData = mock(data.class);
        mockUserDAO = mock(userDAO.class);
        mockAuthDAO = mock(authDAO.class);
        userService = new userS(mockData);

        when(mockData.fetchClientData(userDAO.class)).thenReturn(mockUserDAO);
        when(mockData.fetchClientData(authDAO.class)).thenReturn(mockAuthDAO);
    }

    @Test
    @DisplayName("createUser creates a new user successfully")
    public void testCreateUser_Success() throws DataAccessException {
        user newUser = new user("testUser", "passwordHash", "test@example.com");
        when(mockUserDAO.getUser("testUser")).thenReturn(null);
        when(mockUserDAO.insertUser(any(user.class))).thenReturn(newUser);
        when(mockAuthDAO.insertAuth(any(auth.class))).thenReturn(new auth("authToken", "testUser"));

        auth result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals("authToken", result.authToken());

        verify(mockUserDAO, times(1)).getUser("testUser");
        verify(mockUserDAO, times(1)).insertUser(any(user.class));
        verify(mockAuthDAO, times(1)).insertAuth(any(auth.class));
    }

    @Test
    @DisplayName("createUser fails if user already exists")
    public void testCreateUser_AlreadyExists() throws DataAccessException {
        user existingUser = new user("testUser", "passwordHash", "test@example.com");
        when(mockUserDAO.getUser("testUser")).thenReturn(existingUser);

        auth result = userService.createUser(new user("testUser", "passwordHash", "test@example.com"));

        assertNull(result);

        verify(mockUserDAO, times(1)).getUser("testUser");
        verify(mockUserDAO, times(0)).insertUser(any(user.class));
        verify(mockAuthDAO, times(0)).insertAuth(any(auth.class));
    }

    @Test
    @DisplayName("createUser throws DataAccessException")
    public void testCreateUser_DataAccessException() throws DataAccessException {
        when(mockUserDAO.getUser("testUser")).thenThrow(new DataAccessException("Failed to access data"));

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.createUser(new user("testUser", "passwordHash", "test@example.com"));
        });

        assertEquals("Failed to access data", exception.getMessage());

        verify(mockUserDAO, times(1)).getUser("testUser");
        verify(mockUserDAO, times(0)).insertUser(any(user.class));
        verify(mockAuthDAO, times(0)).insertAuth(any(auth.class));
    }

    @Test
    @DisplayName("deleteAll deletes all users successfully")
    public void testDeleteAll_Success() throws DataAccessException {
        doNothing().when(mockUserDAO).deleteAllUsers();

        userService.deleteAll();

        verify(mockUserDAO, times(1)).deleteAllUsers();
    }

    @Test
    @DisplayName("deleteAll throws DataAccessException")
    public void testDeleteAll_DataAccessException() throws DataAccessException {
        doThrow(new DataAccessException("Failed to delete all users")).when(mockUserDAO).deleteAllUsers();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.deleteAll();
        });

        assertEquals("Failed to delete all users", exception.getMessage());

        verify(mockUserDAO, times(1)).deleteAllUsers();
    }
}