package dataaccess;

import dataaccess.*;
import model.*;

public interface userDAO extends baseDAO{

    user getUser(String username) throws DataAccessException;
    user insertUser(user user) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    void deleteAllUsers() throws DataAccessException;
}