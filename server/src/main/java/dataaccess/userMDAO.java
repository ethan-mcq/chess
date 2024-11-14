package dataaccess;

import model.*;
import java.util.HashMap;
import java.util.Map;

public class userMDAO implements userDAO {

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
    public void deleteAllUsers() throws DataAccessException {
        users.clear();
    }
}
