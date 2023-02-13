package fr.aytronn.moduloapi.api;

import com.google.gson.Gson;
import fr.aytronn.moduloapi.ModuloApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is useful to manage the services
 *
 * @param <T> Service
 *
 * @author HookWoods
 */
public abstract class Connector<T extends Service> {
    private final Map<String, T> services = new ConcurrentHashMap<>();

    /**
     * Register a new service
     *
     * @param service Service
     *
     * @return service
     */
    public T registerService(T service) {
        getServices().put(service.getName(), service);
        return service;
    }

    /**
     * Unregister an existing service
     *
     * @param service Service
     *
     * @return service
     */
    public T unregisterService(T service) {
        getServices().remove(service.getName());
        return service;
    }

    /**
     * Useful to get every database service
     *
     * @return all service
     */
    public Map<String, T> getServices() {
        return this.services;
    }

    /**
     * Useful to get the gson instance
     *
     * @return the gson class
     */
    public Gson getGson() {
        return ModuloApi.getInstance().getGson();
    }
}
