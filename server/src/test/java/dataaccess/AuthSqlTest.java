package dataaccess;

import dataaccess.AuthSqlDai;
import dataaccess.DataAccessException;
import model.Auth;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthSqlTest {

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
    void testInsertAuthPositive() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        Auth insertedAuth = authSqlDai.insertAuth(auth);
        assertNull(insertedAuth);
        assertNotEquals(auth, insertedAuth);
    }

    @Test
    void testInsertAuthNegative() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        authSqlDai.insertAuth(auth);

        // Attempt to insert the same auth token again to trigger a constraint violation
        Auth duplicateAuth = new Auth("token123", "username456");
        Auth result = authSqlDai.insertAuth(duplicateAuth);
        assertNull(result);
    }

    @Test
    void testRemoveAuthPositive() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        authSqlDai.insertAuth(auth);

        Auth removedAuth = authSqlDai.removeAuth("token123");
        assertNotNull(removedAuth);
        assertEquals(auth, removedAuth);
    }

    @Test
    void testRemoveAuthNegative() throws DataAccessException {
        Auth removedAuth = authSqlDai.removeAuth("nonexistentToken");
        assertNull(removedAuth);
    }

    @Test
    void testGetAuthPositive() throws DataAccessException {
        Auth auth = new Auth("token123", "username123");
        authSqlDai.insertAuth(auth);

        Auth retrievedAuth = authSqlDai.getAuth("token123");
        assertNotNull(retrievedAuth);
        assertEquals(auth, retrievedAuth);
    }

    @Test
    void testGetAuthNegative() throws DataAccessException {
        Auth auth = authSqlDai.getAuth("nonexistentToken");
        assertNull(auth);
    }

    @Test
    void testRemoveAllAuthPositive() throws DataAccessException {
        Auth auth1 = new Auth("token123", "username123");
        Auth auth2 = new Auth("token456", "username456");
        authSqlDai.insertAuth(auth1);
        authSqlDai.insertAuth(auth2);

        authSqlDai.removeAuth();

        assertNull(authSqlDai.getAuth("token123"));
        assertNull(authSqlDai.getAuth("token456"));
    }

    @Test
    void testRemoveAllAuthNegative() {
        assertDoesNotThrow(() -> authSqlDai.removeAuth());
    }
}