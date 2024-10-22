package dataaccess;


import model.auth;
import dataaccess.authDAO;
import java.util.HashMap;
import java.util.Map;

public class authMDAO implements authDAO {
    Map<String, auth> authHashMAp = new HashMap<>();

    @Override
    public auth insertAuth(auth auth) throws DataAccessException {
        if(authHashMAp.containsKey(auth.authToken())){
            return null;
        }
        authHashMAp.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public auth removeAuth(String authToken) throws DataAccessException {
        return authHashMAp.remove(authToken);
    }

    @Override
    public auth getAuth(String authToken) throws DataAccessException {
        if(!authHashMAp.containsKey(authToken)){
            return null;
        }
        return authHashMAp.get(authToken);
    }

    @Override
    public void removeAuth() throws DataAccessException {
        authHashMAp.clear();
    }
}
