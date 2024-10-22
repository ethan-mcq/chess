package dataaccess;

import model.auth;
import dataaccess.data;

public interface authDAO extends baseDAO {
    auth insertAuth(auth authData) throws DataAccessException;
    auth removeAuth(String authToken) throws DataAccessException;
    auth getAuth(String authToken) throws DataAccessException;

    void removeAuth() throws DataAccessException;
}