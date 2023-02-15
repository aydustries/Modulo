package fr.aytronn.moduloapi.api;

import fr.aytronn.moduloapi.utils.TimerUtil;

import java.util.TimerTask;

/**
 * This class is useful to auto reconnect the database
 * if the are dead or disconnected
 *
 * @author HookWoods
 */
public abstract class AutoReconnector extends Service {

    private final TimerTask task;

    public AutoReconnector(String name, Settings settings) {
        super(name, settings);
        this.task = run();
        TimerUtil.getTimer().schedule(getTask(), 1000, 1000);
    }

    /**
     * Useful to know if a database is connected
     *
     * @return the connection state to the database
     */
    public abstract boolean isConnected();

    /**
     * reconnect to the database if needed
     */
    public abstract void reconnect();

    /**
     * intern function of this task
     *
     * @return the timertask of this function
     */
    public TimerTask run() {
        return new TimerTask() {
            @Override
            public void run() {
                reconnect();
            }
        };
    }

    /**
     * Useful to know if the service should fall back
     *
     * @return true if the service should fall back, false otherwise
     */
    public boolean shouldFallBack() {
        return !isConnected();
    }

    /**
     * @return the current task
     */
    public TimerTask getTask() {
        return this.task;
    }
}
