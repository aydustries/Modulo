package fr.aytronn.moduloapi.exceptions.module;

/**
 * @author HookWoods
 */
public class InvalidModuleException extends Throwable {
    /**
     * Allow to return a error
     *
     * @param cause Cause of error
     */
    public InvalidModuleException(Throwable cause) {
        super(cause);
    }

    /**
     * Allow to return a error
     *
     * @param message Message of error
     * @param cause Cause of error
     */
    public InvalidModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Allow to return a error
     *
     * @param message Message of error
     */
    public InvalidModuleException(String message) {
        super(message);
    }

}
