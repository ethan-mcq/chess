package dataaccess;

import model.*;

public interface userDAO extends baseDAO{

    user getUser(String username) throws DataAccessException;
    user insertUser(user user) throws DataAccessException;
    void deleteAllUsers() throws DataAccessException;
}