package service;

import dataaccess.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages and provides instances of various service classes.
 */
public class Services {

    private final Map<Class<? extends BaseS>, BaseS> services;
    private final Data data;

    /**
     * Constructs a new Services manager with the given Data access object.
     *
     * @param data The Data access object
     */
    public Services(Data data) {
        this.data = data;
        this.services = new ConcurrentHashMap<>();
        this.generateServices();
    }

    /**
     * Generates and stores instances of all service classes.
     */
    private void generateServices() {
        this.services.put(AuthS.class, new AuthS(this.data));
        this.services.put(UserS.class, new UserS(this.data));
        this.services.put(GameS.class, new GameS(this.data));
    }

    /**
     * Fetches the requested service instance.
     *
     * @param <T> The type of the service
     * @param serviceClass The class object of the service
     * @return The service instance
     * @throws IllegalArgumentException If the requested service is not available
     */
    public <T extends BaseS> T fetchClientService(Class<T> serviceClass) {
        BaseS service = this.services.get(serviceClass);
        if (service == null) {
            throw new IllegalArgumentException("Service unavailable: " + serviceClass.getName());
        }
        return serviceClass.cast(service);
    }
}