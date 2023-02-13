package fr.aytronn.moduloapi.command;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandObject {
    private final String command;

    private String description;

    private final Map<String, SubCommandGroupObject> subCommandGroups;

    public SlashCommandObject(String command) {
        this.command = command;
        this.subCommandGroups = new HashMap<>();
    }

    public SlashCommandObject(String command, String description) {
        this(command);
        this.description = description;
    }

    public String getCommand() {
        return this.command;
    }

    public Map<String, SubCommandGroupObject> getSubCommandGroups() {
        return this.subCommandGroups;
    }

    public void addSubCommandGroup(SubCommandGroupObject subCommandGroupObject) {
        this.subCommandGroups.put(subCommandGroupObject.getSubCommandGroup(), subCommandGroupObject);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        if (this.description == null) {
            return "Empty description";
        }
        return this.description;
    }
}
