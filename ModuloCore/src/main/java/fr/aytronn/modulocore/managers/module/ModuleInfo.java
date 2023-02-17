package fr.aytronn.modulocore.managers.module;

import fr.aytronn.moduloapi.api.module.IModuleInfo;
import fr.aytronn.moduloapi.exceptions.module.InvalidDescriptionException;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * @author HookWoods
 */
public class ModuleInfo implements IModuleInfo {
    private String name;

    private String main;

    private String description;

    private String version;

    private String authors;

    public ModuleInfo(String name, String main, String description, String version, String authors) {
        this.name = name;
        this.main = main;
        this.description = description;
        this.version = version;
        this.authors = authors;
    }

    public ModuleInfo(InputStream stream) throws Exception {
        loadMap(asMap((new Yaml()).load(stream)));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getMain() {
        return this.main;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getAuthorsInLine() {
        return this.authors;
    }

    @Override
    public String[] getAuthors() {
        return this.authors.split(",");
    }

    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
        try {
            this.name = map.get("name").toString();
            if (!this.name.matches("^[A-Za-z0-9 _.-]+$"))
                throw new InvalidDescriptionException("name '" + this.name + "' contains invalid characters.");
            this.name = this.name.replace(' ', '_');
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "name is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "name is of wrong type");
        }
        try {
            this.version = map.get("version").toString();
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "version is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "version is of wrong type");
        }
        try {
            this.main = map.get("main").toString();
            if (this.main.startsWith("org.bukkit.") && this.main.startsWith("org.spigotmc."))
                throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "main is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "main is of wrong type");
        }
        if (map.get("description") != null) {
            this.description = map.get("description").toString();
        } else {
            this.description = "A simple module.";
        }
        if (map.get("authors") != null)
            this.authors = map.get("authors").toString();
    }

    private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
        if (object instanceof Map)
            return (Map<?, ?>) object;
        throw new InvalidDescriptionException(object + " is not properly structured.");
    }
}
