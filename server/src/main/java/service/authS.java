package service;

import dataaccess.*;
import dataaccess.data;
import handler.authH;
import model.auth;
import model.login;
import model.user;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class authS extends baseS {

    public authS(data dataAccess) {
        super(dataAccess);
    }

    public auth login(login loginRequest) throws DataAccessException {
        authDAO authDAO = this.dataAccess.fetchClientData(authDAO.class);
        userDAO userDAO = this.dataAccess.fetchClientData(userDAO.class);

        user user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            return null;
        }

        if(isPasswordMatch(loginRequest.password(), user.password())){
            return authDAO.insertAuth(new auth(generateUUID(), loginRequest.username()));
        }
        return null;

    }

    public auth getAuthData(String authToken) throws DataAccessException {
        authDAO authDAO = this.dataAccess.fetchClientData(authDAO.class);
        return authDAO.getAuth(authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO authDataAccess = this.dataAccess.fetchClientData(authDAO.class);
        authDataAccess.removeAuth(authToken);
    }

    public void deleteAll() throws DataAccessException {
        authDAO auth = this.dataAccess.fetchClientData(authDAO.class);
        auth.removeAuth();
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean isPasswordMatch(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}

