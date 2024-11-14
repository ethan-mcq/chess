package service;

import dataaccess.*;
import model.auth;
import model.login;
import model.user;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class authS extends baseS {

    public authS(data dataAccess) {
        super(dataAccess);
    }

    /**
     * Authenticates the user based on login details.
     * @param loginRequest The login request details
     * @return The authentication token if successful, null otherwise
     * @throws DataAccessException If there is an issue accessing data
     */
    public auth login(login loginRequest) throws DataAccessException {
        authDAO authDAO = this.dataAccess.fetchClientData(authDAO.class);
        userDAO userDAO = this.dataAccess.fetchClientData(userDAO.class);

        user user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            return null;
        }

        if (isPasswordMatch(loginRequest.password(), user.password())) {
            return authDAO.insertAuth(new auth(generateUUID(), loginRequest.username()));
        }
        return null;
    }

    /**
     * Retrieves authentication data associated with the given token.
     * @param authToken The authentication token
     * @return The authentication data
     * @throws DataAccessException If there is an issue accessing data
     */
    public auth getAuthData(String authToken) throws DataAccessException {
        authDAO authDAO = this.dataAccess.fetchClientData(authDAO.class);
        return authDAO.getAuth(authToken);
    }

    /**
     * Logs out the user by removing the authentication token.
     *
     * @param authToken The authentication token
     * @return
     * @throws DataAccessException If there is an issue accessing data
     */
    public auth logout(String authToken) throws DataAccessException {
        authDAO authDataAccess = this.dataAccess.fetchClientData(authDAO.class);
        return authDataAccess.removeAuth(authToken);
    }

    /**
     * Deletes all authentication tokens.
     * @throws DataAccessException If there is an issue accessing data
     */
    public void deleteAll() throws DataAccessException {
        authDAO authDataAccess = this.dataAccess.fetchClientData(authDAO.class);
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