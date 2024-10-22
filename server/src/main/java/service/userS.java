package service;

import dataaccess.*;
import model.*;

/**
 * Service class for handling user-related operations.
 */
public class userS extends baseS {

    /**
     * Constructs a new user service with the given data access object.
     *
     * @param dataAccess The data access object
     */
    public userS(data dataAccess) {
        super(dataAccess);
    }

    /**
     * Creates a new user and returns an authentication token.
     *
     * @param user The user information
     * @return The authentication data if user creation is successful, null if the username is already taken
     * @throws DataAccessException If there is an issue accessing data
     */
    public auth createUser(user user) throws DataAccessException {
        userDAO userDataAccess = this.dataAccess.fetchClientData(userDAO.class);

        if (userDataAccess.getUser(user.username()) != null) {
            return null;
        }

        user newUser = new user(user.username(), authS.hashPassword(user.password()), user.email());
        userDataAccess.insertUser(newUser);

        authDAO authDataAccess = this.dataAccess.fetchClientData(authDAO.class);
        return authDataAccess.insertAuth(new auth(authS.generateUUID(), newUser.username()));
    }

    /**
     * Deletes all users.
     *
     * @throws DataAccessException If there is an issue accessing data
     */
    public void deleteAll() throws DataAccessException {
        userDAO userDataAccess = this.dataAccess.fetchClientData(userDAO.class);
        userDataAccess.deleteAllUsers();
    }
}