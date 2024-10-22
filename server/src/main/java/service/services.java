package service;

import dataaccess.data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages and provides instances of various service classes.
 */
public class services {

    private final Map<Class<? extends baseS>, baseS> services;
    private final data data;

    /**
     * Constructs a new services manager with the given data access object.
     *
     * @param data The data access object
     */
    public services(data data) {
        this.data = data;
        this.services = new ConcurrentHashMap<>();
        this.generateServices();
    }

    /**
     * Generates and stores instances of all service classes.
     */
    private void generateServices() {
        this.services.put(authS.class, new authS(this.data));
        this.services.put(userS.class, new userS(this.data));
        this.services.put(gameS.class, new gameS(this.data));
    }

    /**
     * Fetches the requested service instance.
     *
     * @param <T> The type of the service
     * @param serviceClass The class object of the service
     * @return The service instance
     * @throws IllegalArgumentException If the requested service is not available
     */
    public <T extends baseS> T fetchClientService(Class<T> serviceClass) {
        baseS service = this.services.get(serviceClass);
        if (service == null) {
            throw new IllegalArgumentException("Service unavailable: " + serviceClass.getName());
        }
        return serviceClass.cast(service);
    }
}