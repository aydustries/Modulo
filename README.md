# Modulo
Create your bot discord more easily with Modulo. Allows you to add features easily with modules.
You can see it as a minecraft server with plugins. Modulo will load the modules you added in the modules folder.

## Summary

- [How to build](https://github.com/aytronnn/Modulo/wiki/Modulo#how-to-build)
- [How to deploy](https://github.com/aytronnn/Modulo/wiki/Modulo#how-to-deploy)
- [How To Developers](https://github.com/aytronnn/Modulo/wiki/Modulo#how-to-developers)
- [Basic Usage](#basic-usage)

## Basic Usage

### How to add the dependency
```xml
<repository>
    <url>"https://maven.pkg.github.com/aytronnn/Modulo"</url>
</repository>
```
* Artifact Information:
```xml
<dependency>
    <groupId>fr.aytronn</groupId>
    <artifactId>modulo-api</artifactId>
    <version>latest</version>
</dependency>
 ```

**Or alternatively, with Gradle:**

* Repository:
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/aytronnn/Modulo")
    }
}
dependencies {
    compileOnly("fr.aytronn:modulo-api:latest")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(19))
}
```

### Create a module

```java
public class Example extends IModule {
    private static Example instance;
    private Config config;

    private TorrentManager torrentManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting Example...");

        registerListeners();
        registerCommands();
    }

    public void registerListeners() {
        registerListener(new ExampleListener());
    }

    public void registerCommands() {
        registerCommand(new ExampleCommand());
    }

    public static PlexFTH getInstance() {
        return instance;
    }
}
```

### Create a command

![](https://user-images.githubusercontent.com/72011165/218261719-9d8428fb-9589-4634-a94e-75f6d8ae199d.png)

```java
public class ExampleCommand {

    @Command(name = "plexfth.clear", subCommandObject = {"nbMessage"}, subCommandType = {SlashCommandOptionType.DECIMAL}, description = "Clear the channel")
    public void clearCommand(CommandArgs arg) {
        //Do some code for clear the channel
        
        //Mandatory to respond to the command
        arg.getCommandInteraction().createImmediateResponder()
                .setContent("Clear command")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }
}
```

### Create a listener

```java
public class ExampleListener implements MessageCreateListener {
    
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        event.getChannel().sendMessage("Thank you for message! <@" + event.getMessageAuthor().getIdAsString() + ">");
    }
}
```

### Credits

For more information, see the [wiki](https://javacord.org/wiki/basic-tutorials/interactions/commands.html#creating-a-command) of [Javacord](https://github.com/Javacord/Javacord) this is the api I use to make the CoreBot.
