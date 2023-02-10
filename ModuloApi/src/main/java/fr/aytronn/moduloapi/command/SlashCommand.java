package fr.aytronn.moduloapi.command;

import java.util.HashMap;
import java.util.Map;

public class SlashCommand {
    private final String command;

    private String description;

    private final Map<String, SubCommandGroup> subCommandGroups;

    public SlashCommand(String command) {
        this.command = command;
        this.subCommandGroups = new HashMap<>();
    }

    public SlashCommand(String command, String description) {
        this(command);
        this.description = description;
    }

    public String getCommand() {
        return this.command;
    }

    public Map<String, SubCommandGroup> getSubCommandGroups() {
        return this.subCommandGroups;
    }

    public void addSubCommandGroup(SubCommandGroup subCommandGroup) {
        this.subCommandGroups.put(subCommandGroup.getSubCommandGroup(), subCommandGroup);
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
