package service;

import dataaccess.*;
import model.*;

/**
 * Service class for handling user-related operations.
 */
public class UserS extends BaseS {

    /**
     * Constructs a new user service with the given Data access object.
     *
     * @param dataAccess The Data access object
     */
    public UserS(Data dataAccess) {
        super(dataAccess);
    }

    /**
     * Creates a new user and returns an authentication token.
     *
     * @param user The user information
     * @return The authentication Data if user creation is successful, null if the username is already taken
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Auth createUser(UserM user) throws DataAccessException {
        UserDao userDataAccess = this.dataAccess.fetchClientData(UserDao.class);

        if (userDataAccess.getUser(user.username()) != null) {
            return null;
        }

        UserM newUser = new UserM(user.username(), AuthS.hashPassword(user.password()), user.email());
        userDataAccess.insertUser(newUser);

        AuthDao authDataAccess = this.dataAccess.fetchClientData(AuthDao.class);
        return authDataAccess.insertAuth(new Auth(AuthS.generateUUID(), newUser.username()));
    }

    /**
     * Deletes all users.
     *
     * @throws DataAccessException If there is an issue accessing Data
     */
    public void deleteAll() throws DataAccessException {
        UserDao userDataAccess = this.dataAccess.fetchClientData(UserDao.class);
        userDataAccess.deleteAllUsers();
    }
}