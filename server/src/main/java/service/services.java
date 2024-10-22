package service;

import dataaccess.data;
import service.baseS;
import service.authS;
import service.gameS;
import service.userS;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class services {

    private final Map<Class<? extends baseS>, baseS> services;
    private final data data;

    public services(data data) {
        this.data = data;
        services = new ConcurrentHashMap<>();
        this.generateServices();
    }

    private void generateServices(){
        this.services.put(authS.class, new authS(data));
        this.services.put(userS.class, new userS(data));
        this.services.put(gameS.class, new gameS(data));
    }

    public <T extends baseS> T fetchClientService(Class<T> authClass) {
        baseS service = services.get(authClass);
        if (service == null) {
            throw new IllegalArgumentException("Service unavailable: " + authClass.getName());
        }
        return authClass.cast(service);
    }
}
