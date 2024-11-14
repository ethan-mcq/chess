package dataaccess;

import model.Auth;

public interface AuthDao extends BaseDao {
    Auth insertAuth(Auth authData) throws DataAccessException;
    Auth removeAuth(String authToken) throws DataAccessException;
    Auth getAuth(String authToken) throws DataAccessException;

    void removeAuth() throws DataAccessException; // failed to compile?
}