package fr.aytronn.moduloapi.modules;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class ModuleLogger extends Logger {

    /**
     * Allow to get the logger
     *
     * @param description Description
     *
     * @return The logger
     */
    public static Logger getLogger(IModuleInfo description) {
        Logger logger = new ModuleLogger(description);
        if (!LogManager.getLogManager().addLogger(logger)) {
            logger = LogManager.getLogManager().getLogger("Module - " + description.getName());
        }

        return logger;
    }

    /**
     * Module logger
     *
     * @param description Description of modules
     */
    private ModuleLogger(IModuleInfo description) {
        super("Module - " + description.getName(), null);
    }

    /**
     * Allow to set parent
     *
     * @param parent Logger
     */
    @Override
    public void setParent(Logger parent) {
        if (getParent() != null) {
            warning("Ignoring attempt to change parent of plugin logger");
        } else {
            this.log(Level.FINE, "Setting plugin logger parent to {0}", parent);
            super.setParent(parent);
        }
    }

}

