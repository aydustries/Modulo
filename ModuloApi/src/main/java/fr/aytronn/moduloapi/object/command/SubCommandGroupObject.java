package fr.aytronn.moduloapi.object.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SubCommandGroupObject {

    private final String subCommandGroup;

    private String description;

    private final Map<String, SubCommandObject> subCommands;

    private boolean required = true;

    public SubCommandGroupObject(String subCommandGroup) {
        this.subCommandGroup = subCommandGroup;
        this.subCommands = new HashMap<>();
    }

    public SubCommandGroupObject(String command, String description) {
        this(command);
        this.description = description;
    }

    public String getSubCommandGroup() {
        return this.subCommandGroup;
    }

    public Map<String, SubCommandObject> getSubCommands() {
        return this.subCommands;
    }

    public void addSubCommand(SubCommandObject subCommandObject) {
        this.subCommands.put(subCommandObject.getSubCommand(), subCommandObject);
    }

    public String getDescription() {
        return Objects.requireNonNullElse(this.description, "Empty description");
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
