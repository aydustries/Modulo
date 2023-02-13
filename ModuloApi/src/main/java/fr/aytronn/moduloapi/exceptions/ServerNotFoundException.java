package fr.aytronn.moduloapi.exceptions;

public class ServerNotFoundException extends Exception {

    /**
     * Allow to return a error
     *
     * @param cause Cause of error
     */
    public ServerNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Allow to return a error
     *
     * @param message Message of error
     * @param cause Cause of error
     */
    public ServerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Allow to return a error
     *
     * @param message Message of error
     */
    public ServerNotFoundException(String message) {
        super(message);
    }
}
