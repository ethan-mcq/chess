package dataaccess;

import model.auth;

public interface authDAO extends baseDAO {
    auth insertAuth(auth authData) throws DataAccessException;
    auth removeAuth(String authToken) throws DataAccessException;
    auth getAuth(String authToken) throws DataAccessException;

    void removeAuth() throws DataAccessException;
}