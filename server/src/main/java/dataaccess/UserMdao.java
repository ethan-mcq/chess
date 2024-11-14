package dataaccess;

import model.*;
import java.util.HashMap;
import java.util.Map;

public class UserMdao implements UserDao {

    Map<String, UserM> users = new HashMap<>();

    @Override
    public UserM getUser(String username) throws DataAccessException {
        if(users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }

    @Override
    public UserM insertUser(UserM user) throws DataAccessException {
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
