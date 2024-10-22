package dataaccess;

import dataaccess.*;
import model.*;
import java.util.HashMap;
import java.util.Map;

public class userMDAO implements userDAO {
    // Username, and UserData
    Map<String, user> users = new HashMap<>();

    @Override
    public user getUser(String username) throws DataAccessException {
        if(users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }


    @Override
    public user insertUser(user user) throws DataAccessException {
        if(users.containsKey(user.username())){
            return null;
        }
        users.put(user.username(), user);
        return user;
    }
    @Override
    public void deleteUser(String username) throws DataAccessException {
        users.remove(username);
    }
    @Override
    public void deleteAllUsers() throws DataAccessException {
        users.clear();
    }
}
