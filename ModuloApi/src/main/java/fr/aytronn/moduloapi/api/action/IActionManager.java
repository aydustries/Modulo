package fr.aytronn.moduloapi.api.action;

public interface IActionManager {

    /**
     * This function is used to register an action
     * The method must be annotated with @Action
     *
     * @param classAction the action to register
     */
    void registerAction(Object classAction);

    /**
     * This function is used to unregister an action
     *
     * @param action the action to unregister
     */
    void unregisterAction(Object action);
}
