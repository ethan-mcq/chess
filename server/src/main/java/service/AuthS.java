package service;

import dataaccess.*;
import model.Auth;
import model.Login;
import model.UserM;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class AuthS extends BaseS {

    public AuthS(Data dataAccess) {
        super(dataAccess);
    }

    /**
     * Authenticates the user based on Login details.
     * @param loginRequest The Login request details
     * @return The authentication token if successful, null otherwise
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Auth login(Login loginRequest) throws DataAccessException {
        AuthDao authDAO = this.dataAccess.fetchClientData(AuthDao.class);
        UserDao userDAO = this.dataAccess.fetchClientData(UserDao.class);

        UserM user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            return null;
        }

        if (isPasswordMatch(loginRequest.password(), user.password())) {
            return authDAO.insertAuth(new Auth(generateUUID(), loginRequest.username()));
        }
        return null;
    }

    /**
     * Retrieves authentication Data associated with the given token.
     * @param authToken The authentication token
     * @return The authentication Data
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Auth getAuthData(String authToken) throws DataAccessException {
        AuthDao authDAO = this.dataAccess.fetchClientData(AuthDao.class);
        return authDAO.getAuth(authToken);
    }

    /**
     * Logs out the user by removing the authentication token.
     *
     * @param authToken The authentication token
     * @return
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Auth logout(String authToken) throws DataAccessException {
        AuthDao authDataAccess = this.dataAccess.fetchClientData(AuthDao.class);
        return authDataAccess.removeAuth(authToken);
    }

    /**
     * Deletes all authentication tokens.
     * @throws DataAccessException If there is an issue accessing Data
     */
    public void deleteAll() throws DataAccessException {
        AuthDao authDataAccess = this.dataAccess.fetchClientData(AuthDao.class);
        authDataAccess.removeAuth();
    }

    /**
     * Generates a new UUID.
     * @return A new UUID as a string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Hashes the given password using BCrypt.
     * @param password The password to be hashed
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks if the given password matches the hashed password.
     * @param password The plain text password
     * @param hashedPassword The hashed password
     * @return true if the passwords match, false otherwise
     */
    public static boolean isPasswordMatch(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}