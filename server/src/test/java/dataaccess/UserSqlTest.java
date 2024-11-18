package src.test.java.dataaccess;

import dataaccess.UserSqlDai;
import dataaccess.DataAccessException;
import model.UserM;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserSqlTest {

    private UserSqlDai userSqlDai;

    @BeforeEach
    void setUp() throws DataAccessException {
        userSqlDai = new UserSqlDai();
        userSqlDai.deleteAllUsers(); // Ensure clean state before each test
    }

    @AfterEach
    void tearDown() {
        userSqlDai = null;
    }

    @Test
    void testInsertUser_Positive() throws DataAccessException {
        UserM user = new UserM("username123", "password123", "email@example.com");
        UserM insertedUser = userSqlDai.insertUser(user);
        assertNotNull(insertedUser);
        assertEquals(user, insertedUser);
    }

    @Test
    void testInsertUser_Negative() throws DataAccessException {
        UserM user = new UserM("username123", "password123", "email@example.com");
        userSqlDai.insertUser(user);

        // Attempt to insert the same username again to trigger a constraint violation
        UserM duplicateUser = new UserM("username123", "password456", "another@example.com");
        assertThrows(DataAccessException.class, () -> {
            userSqlDai.insertUser(duplicateUser);
        });
    }

    @Test
    void testGetUser_Positive() throws DataAccessException {
        UserM user = new UserM("username123", "password123", "email@example.com");
        userSqlDai.insertUser(user);

        UserM retrievedUser = userSqlDai.getUser("username123");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    @Test
    void testGetUser_Negative() throws DataAccessException {
        UserM user = userSqlDai.getUser("nonexistentUser");
        assertNull(user);
    }

    @Test
    void testdeleteAllUsers_Positive() throws DataAccessException {
        UserM user1 = new UserM("username123", "password123", "email1@example.com");
        UserM user2 = new UserM("username456", "password456", "email2@example.com");
        userSqlDai.insertUser(user1);
        userSqlDai.insertUser(user2);

        userSqlDai.deleteAllUsers();

        assertNull(userSqlDai.getUser("username123"));
        assertNull(userSqlDai.getUser("username456"));
    }

    @Test
    void testdeleteAllUsers_Negative() {
        assertDoesNotThrow(() -> userSqlDai.deleteAllUsers());
    }
}