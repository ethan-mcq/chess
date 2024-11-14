package dataaccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class data {
    private final dataTypes dataTypes;
    private final Map<Class<? extends baseDAO>, baseDAO> clientData;

    public data(dataTypes dataTypes) throws DataAccessException {
        this.dataTypes = dataTypes;
        this.clientData = new ConcurrentHashMap<>();
        this.initializeClientData();
    }

    private void initializeClientData() throws DataAccessException {
        if (dataTypes == dataaccess.dataTypes.MEM_DATA) {
            clientData.put(gameDAO.class, new gameMDAO());
            clientData.put(authDAO.class, new authMDAO());
            clientData.put(userDAO.class, new userMDAO());
        }else{
            DatabaseManager.Create();
            clientData.put(userDAO.class, new UserSqlDai());
            clientData.put(gameDAO.class, new GameSqlDai());
            clientData.put(authDAO.class, new AuthSqlDai());
        }
    }

    public <T extends baseDAO> T fetchClientData(Class<T> dataAccessClass) throws DataAccessException {
        baseDAO service = clientData.get(dataAccessClass);
        if (service == null) {
            String errorMessage = "Service for " + dataAccessClass.getSimpleName() + " is unavailable.";
            throw new DataAccessException(errorMessage);
        }
        return dataAccessClass.cast(service);
    }
}