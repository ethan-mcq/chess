package dataaccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class data {
    private final dataTypes dataTypes;
    private final Map<Class<? extends baseDAO>, baseDAO> clientData = new ConcurrentHashMap<>();

    public data(dataTypes dataTypes) {
        this.dataTypes = dataTypes;
        initializeClientData();
    }

    private void initializeClientData() {
        if (dataTypes == dataaccess.dataTypes.MEM_DATA) {
            clientData.put(gameDAO.class, new gameMDAO());
            clientData.put(authDAO.class, new authMDAO());
            clientData.put(userDAO.class, new userMDAO());
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