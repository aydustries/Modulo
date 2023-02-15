package fr.aytronn.moduloapi.api;

/**
 * @author HookWoods
 */
public abstract class Service {

    private final String name;
    private final Settings settings;
    private boolean dead;

    public Service(String name, Settings settings) {
        this.name = name;
        this.settings = settings;
    }

    /**
     * Used to remove database connection
     */
    public abstract void remove();

    /**
     * Used to check if the service is connected
     *
     * @return true if connected, false otherwise
     */
    public boolean isAlive() {
        return !isDead();
    }

    /**
     * Useful to know if the service should fall back
     *
     * @return true if the service should fall back, false otherwise
     */
    public boolean isDead() {
        return this.dead;
    }

    /**
     * Used to set the service as dead
     */
    public void setDead() {
        this.dead = true;
    }

    /**
     * Used to set the service as alive
     *
     * @return Settings
     */
    public Settings getSettings() {
        return this.settings;
    }

    /**
     * Gets the name of the service
     *
     * @return the name of the service
     */
    public String getName() {
        return this.name;
    }
}
