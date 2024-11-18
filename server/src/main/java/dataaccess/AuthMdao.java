package dataaccess;

import model.*;
import java.util.HashMap;
import java.util.Map;

public class AuthMdao implements AuthDao {
    Map<String, Auth> authHash = new HashMap<>();

    @Override
    public Auth getAuth(String authToken) throws DataAccessException {
        if(!authHash.containsKey(authToken)){
            return null;
        }
        return authHash.get(authToken);
    }

    @Override
    public Auth insertAuth(Auth auth) throws DataAccessException {
        if(authHash.containsKey(auth.authToken())){
            return null;
        }
        authHash.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public Auth removeAuth(String authToken) throws DataAccessException {
        return authHash.remove(authToken);
    }
    @Override
    public void removeAuth() throws DataAccessException {
        authHash.clear();
    }
}
