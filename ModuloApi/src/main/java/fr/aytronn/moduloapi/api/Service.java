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

    public abstract void remove();

    public boolean isAlive() {
        return !isDead();
    }

    public boolean isDead() {
        return this.dead;
    }

    public void setDead() {
        this.dead = true;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public String getName() {
        return this.name;
    }
}
