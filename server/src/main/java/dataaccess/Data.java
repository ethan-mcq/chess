package dataaccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    private final DataType DataType;
    private final Map<Class<? extends BaseDao>, BaseDao> clientData;

    public Data(DataType DataType) throws DataAccessException {
        this.DataType = DataType;
        this.clientData = new ConcurrentHashMap<>();
        this.initializeClientData();
    }

    private void initializeClientData() throws DataAccessException {
        if (DataType == DataType.MEM_DATA) {
            clientData.put(GameDao.class, new GameMdao());
            clientData.put(AuthDao.class, new AuthMdao());
            clientData.put(UserDao.class, new UserMdao());
        }else{
            DatabaseManager.createDatabase();
            clientData.put(UserDao.class, new UserSqlDai());
            clientData.put(GameDao.class, new GameSqlDai());
            clientData.put(AuthDao.class, new AuthSqlDai());
        }
    }

    public <T extends BaseDao> T fetchClientData(Class<T> dataAccessClass) throws DataAccessException {
        BaseDao service = clientData.get(dataAccessClass);
        if (service == null) {
            String errorMessage = "Service for " + dataAccessClass.getSimpleName() + " is unavailable.";
            throw new DataAccessException(errorMessage);
        }
        return dataAccessClass.cast(service);
    }
}