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
    void testInsertUserPositive() throws DataAccessException {
        UserM user = new UserM("username123", "password123", "email@example.com");
        UserM insertedUser = userSqlDai.insertUser(user);
        assertNotNull(insertedUser);
        assertEquals(user, insertedUser);
    }

    @Test
    void testInsertUserNegative() throws DataAccessException {
        UserM user = new UserM("username123", "password123", "email@example.com");
        userSqlDai.insertUser(user);

        // Attempt to insert the same username again to trigger a constraint violation
        UserM duplicateUser = new UserM("username123", "password456", "another@example.com");
        assertThrows(DataAccessException.class, () -> {
            userSqlDai.insertUser(duplicateUser);
        });
    }

    @Test
    void testGetUserPositive() throws DataAccessException {
        UserM user = new UserM("username123", "password123", "email@example.com");
        userSqlDai.insertUser(user);

        UserM retrievedUser = userSqlDai.getUser("username123");
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    @Test
    void testGetUserNegative() throws DataAccessException {
        UserM user = userSqlDai.getUser("nonexistentUser");
        assertNull(user);
    }

    @Test
    void testdeleteAllUsersPositive() throws DataAccessException {
        UserM user1 = new UserM("username123", "password123", "email1@example.com");
        UserM user2 = new UserM("username456", "password456", "email2@example.com");
        userSqlDai.insertUser(user1);
        userSqlDai.insertUser(user2);

        userSqlDai.deleteAllUsers();

        assertNull(userSqlDai.getUser("username123"));
        assertNull(userSqlDai.getUser("username456"));
    }

    @Test
    void testdeleteAllUsersNegative() {
        assertDoesNotThrow(() -> userSqlDai.deleteAllUsers());
    }
}