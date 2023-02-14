# Modulo [![CI](https://github.com/aydustries/Modulo/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/aydustries/Modulo/actions/workflows/ci.yml) [![DOCKER HUB](https://github.com/aydustries/Modulo/actions/workflows/publish-docker-hub.yml/badge.svg?branch=master)](https://hub.docker.com/repository/docker/aytronn/modulo/general) [![Publish Nexus](https://github.com/aydustries/Modulo/actions/workflows/gradle-publish.yml/badge.svg?branch=master)](http://nexus.aytronn.com/#browse/browse:aydustries:fr%2Faytronn%2Fmodulo-api%2Fmaster) [![Latest version](https://shields.io/github/release/aydustries/Modulo.svg?label=Version&colorB=brightgreen&style=flat-square)](https://github.com/aydustries/Modulo/releases/latest) [![Discord server](https://shields.io/discord/281078252599246850.svg?colorB=%237289DA&label=Discord&style=flat-square)](https://discord.gg/nutFJyJDvM)
Create your bot discord more easily with Modulo. Allows you to add features easily with modules.
You can see it as a minecraft server with plugins. Modulo will load the modules you added in the modules folder.

## Summary

- [How to build](https://github.com/aydustries/Modulo/wiki/Modulo#how-to-build)
- [How to deploy](https://github.com/aydustries/Modulo/wiki/Modulo#how-to-deploy)
- [How To Developers](https://github.com/aydustries/Modulo/wiki/Modulo#how-to-developers)
- [Basic Usage](#basic-usage)
- [Credits](#credits)

## Basic Usage

### How to add the dependency
```xml
<repository>
    <url>"http://nexus.aytronn.com/repository/aydustries/"</url>
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
        name = "aydustries"
        url = uri("http://nexus.aytronn.com/repository/aydustries/")
        allowInsecureProtocol = true
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

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting Example...");

        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutdown Example...");
    }

    public void registerListeners() {
        registerListener(new ExampleListener());
    }

    public void registerCommands() {
        registerCommand(new ExampleCommand());
    }

    public static Example getInstance() {
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
        arg.reply("Clear command");
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

## Credits

For more information, see the [wiki](https://javacord.org/wiki/basic-tutorials/interactions/commands.html#creating-a-command) of [Javacord](https://github.com/Javacord/Javacord) this is the api I use to make the CoreBot.
For the module loader the code comes from [@HookWoods](https://github.com/HookWoods)
