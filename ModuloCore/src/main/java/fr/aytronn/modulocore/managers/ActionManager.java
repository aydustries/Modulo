package fr.aytronn.modulocore.managers;

import fr.aytronn.moduloapi.api.action.Action;
import fr.aytronn.moduloapi.api.action.ActionArgs;
import fr.aytronn.moduloapi.api.action.IActionManager;
import fr.aytronn.modulocore.ModuloCore;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class ActionManager implements IActionManager {

    private final Map<String, Map.Entry<Method, Object>> methods;

    public ActionManager() {
        this.methods = new HashMap<>();
    }

    @Override
    public void registerAction(Object classAction) {
        for (final var m : classAction.getClass().getMethods()) {
            if (m.getAnnotation(Action.class) == null) continue;

            if (m.getParameterTypes().length > 1 || m.getParameterTypes()[0] != ActionArgs.class) {
                ModuloCore.getInstance().getLogger().warn("Unable to register action " + m.getName() + ". Unexpected method arguments");
                continue;
            }

            final Action action = m.getAnnotation(Action.class);
            if (action == null) continue;

            getMethods().put(action.customId(), new AbstractMap.SimpleEntry<>(m, classAction));
        }
    }

    @Override
    public void unregisterAction(Object action) {
        for (final var m : action.getClass().getMethods()) {
            if (m.getAnnotation(Action.class) == null) continue;

            final Action actionAnnotation = m.getAnnotation(Action.class);
            if (actionAnnotation == null) continue;

            getMethods().remove(actionAnnotation.customId());
        }
    }

    public Map<String, Map.Entry<Method, Object>> getMethods() {
        return this.methods;
    }
}
