# Modulo
Create your bot discord more easily with Modulo. Allows you to add features easily with modules.
You can see it as a minecraft server with plugins. Modulo will load the modules you added in the modules folder.

## Summary

- [How to build](#how-to-build)
- [How to deploy](#how-to-deploy)
- [How To Developers](#how-to-developers)
- [Basic Usage](#basic-usage)

## How to build

```sh
./gradlew clean shadowJar
```

### Environment variables

- `PROJECT_VERSION` - the version of the project - default: `develop`
- `USERNAME` - the maven user - default: ``
- `TOKEN` - the maven pass - default: ``
- `REPO_URL` - the maven repo - default: ``

<br/>
This will build the project.

## How to deploy

### How to deploy the jar

To publish the jar on the maven repo, you need to set the environment variables.

```sh
./gradlew clean build publish
```

To publish the docker image, you need to login to docker hub.

```sh
echo yourPassword | docker login -u yourUsername --password-stdin
```

And then you can publish the docker image.

```sh
./gradlew clean shadowJar dockerPushDockerHub
```

## How To Developers
------
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

## Basic Usage

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

![](https://github.com/aytronnn/Modulo/blob/master/img/discord_command.png)

```java
public class ExampleCommand {

    @Command(name = "plexfth.clear", subCommand = {"nbMessage"}, subCommandType = {SlashCommandOptionType.DECIMAL}, description = "Clear the channel")
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

For more information, see the [wiki](https://javacord.org/wiki/basic-tutorials/interactions/commands.html#creating-a-command) of [Javacord](https://github.com/Javacord/Javacord) this is the api I use to make the CoreBot.



