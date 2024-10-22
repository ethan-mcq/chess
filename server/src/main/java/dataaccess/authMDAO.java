package dataaccess;

import model.*;
import java.util.HashMap;
import java.util.Map;

public class authMDAO implements authDAO {
    Map<String, auth> authHash = new HashMap<>();

    @Override
    public auth getAuth(String authToken) throws DataAccessException {
        if(!authHash.containsKey(authToken)){
            return null;
        }
        return authHash.get(authToken);
    }

    @Override
    public auth insertAuth(auth auth) throws DataAccessException {
        if(authHash.containsKey(auth.authToken())){
            return null;
        }
        authHash.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public auth removeAuth(String authToken) throws DataAccessException {
        return authHash.remove(authToken);
    }
    @Override
    public void removeAuth() throws DataAccessException {
        authHash.clear();
    }
}
