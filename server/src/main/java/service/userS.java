package service;

import dataaccess.*;
import model.*;

public class userS extends baseS {
    public userS(data dataAccess) {
        super(dataAccess);
    }


    public auth createUser(user user) throws DataAccessException {
        userDAO userDataAccess = this.dataAccess.fetchClientData(userDAO.class);
        if(userDataAccess.getUser(user.username()) != null){
            return null;
        }
        user newUser = new user(user.username(), authS.hashPassword(user.password()), user.email());
        userDataAccess.insertUser(newUser);

        authDAO authDataAccess = this.dataAccess.fetchClientData(authDAO.class);

        return authDataAccess.insertAuth(
                new auth(authS.generateUUID(), newUser.username())
        );
    }
    public void deleteAll() throws DataAccessException {
        userDAO userDataAccess = this.dataAccess.fetchClientData(userDAO.class);
        userDataAccess.deleteAllUsers();
    }
}
