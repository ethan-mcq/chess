package src.test.java.dataaccess;

import dataaccess.AuthSqlDai;
import dataaccess.DataAccessException;
import model.Auth;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthSqlDaiTest {

    private AuthSqlDai authSqlDai;

    @BeforeEach
    void setUp() throws DataAccessException {
        authSqlDai = new AuthSqlDai();
    }

    @AfterEach
    void tearDown() {
        authSqlDai = null;
    }

    @Test
    void testInsertAuth_Positive() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        Auth insertedAuth = authSqlDai.insertAuth(auth);
        assertNotNull(insertedAuth);
        assertEquals(auth, insertedAuth);
    }

    @Test
    void testInsertAuth_Negative() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        authSqlDai.insertAuth(auth);

        // Attempt to insert the same auth token again to trigger a constraint violation
        Auth duplicateAuth = new Auth("token123", "username456");
        Auth result = authSqlDai.insertAuth(duplicateAuth);
        assertNull(result);
    }

    @Test
    void testRemoveAuth_Positive() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        authSqlDai.insertAuth(auth);

        Auth removedAuth = authSqlDai.removeAuth("token123");
        assertNotNull(removedAuth);
        assertEquals(auth, removedAuth);
    }

    @Test
    void testRemoveAuth_Negative() throws DataAccessException {
        Auth removedAuth = authSqlDai.removeAuth("nonexistentToken");
        assertNull(removedAuth);
    }

    @Test
    void testGetAuth_Positive() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        authSqlDai.insertAuth(auth);

        Auth retrievedAuth = authSqlDai.getAuth("token123");
        assertNotNull(retrievedAuth);
        assertEquals(auth, retrievedAuth);
    }

    @Test
    void testGetAuth_Negative() throws DataAccessException {
        Auth auth = authSqlDai.getAuth("nonexistentToken");
        assertNull(auth);
    }

    @Test
    void testRemoveAllAuth_Positive() throws DataAccessException {
        Auth auth1 = new Auth("token123", "username123");
        Auth auth2 = new Auth("token456", "username456");
        authSqlDai.insertAuth(auth1);
        authSqlDai.insertAuth(auth2);

        authSqlDai.removeAuth();

        assertNull(authSqlDai.getAuth("token123"));
        assertNull(authSqlDai.getAuth("token456"));
    }

    @Test
    void testRemoveAllAuth_Negative() {
        assertDoesNotThrow(() -> authSqlDai.removeAuth());
    }
}