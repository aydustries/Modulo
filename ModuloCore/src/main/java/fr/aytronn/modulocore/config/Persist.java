package fr.aytronn.modulocore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.aytronn.moduloapi.config.IDiscUtil;
import fr.aytronn.moduloapi.config.IPersist;
import fr.aytronn.moduloapi.utils.threads.ZakaryThread;
import fr.aytronn.modulocore.ModuloCore;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.concurrent.CompletableFuture;

/**
 * @author HookWoods
 */
public class Persist implements IPersist {

    private final Gson gson;
    private final IDiscUtil discUtil;

    public Persist() {
        this.gson = buildGson().create();
        this.discUtil = new DiscUtil();
    }

    @Override
    public String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    @Override
    public File getFile(String name) {
        return new File(ModuloCore.getInstance().getDataFolder(), name + ".json");
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(Object instance, File file) {
        final Runnable runnable = () -> {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.discUtil.writeCatch(file, this.gson.toJson(instance));
        };
        ZakaryThread.FILE_EXECUTOR.execute(runnable);
    }

    @Override
    public <T> T load(Class<T> clazz, File file) {
        final var object = basicLoad(clazz, file);
        save(object, file);
        return object;
    }

    @Override
    public String load(File file) {
        return CompletableFuture.supplyAsync(() -> this.discUtil.readCatch(file), ZakaryThread.FILE_EXECUTOR.get()).join();
    }

    @Override
    public <T> T load(Class<T> clazz, String content) {
        final var object = basicLoad(clazz, content);
        save(object);
        return object;
    }

    private <T> T basicLoad(Class<T> clazz, File file) {
        final CompletableFuture<T> completableFuture = CompletableFuture.supplyAsync(() -> {
            final var content = this.discUtil.readCatch(file);
            if (content == null) {
                return null;
            }

            try {
                return this.gson.fromJson(content, clazz);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }, ZakaryThread.FILE_EXECUTOR.get());
        return completableFuture.join();
    }

    private <T> T basicLoad(Class<T> clazz, String content) {
        final CompletableFuture<T> completableFuture = CompletableFuture.supplyAsync(() -> {
            if (content == null || content.isEmpty()) {
                return null;
            }

            try {
                return this.gson.fromJson(content, clazz);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }, ZakaryThread.FILE_EXECUTOR.get());
        return completableFuture.join();
    }

    private GsonBuilder buildGson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }
}