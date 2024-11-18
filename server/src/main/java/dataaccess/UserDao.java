package dataaccess;

import model.*;

public interface UserDao extends BaseDao {

    UserM getUser(String username) throws DataAccessException;
    UserM insertUser(UserM user) throws DataAccessException;
    void deleteAllUsers() throws DataAccessException;
}