package fr.aytronn.modulocore.config;

import com.google.common.io.Files;
import fr.aytronn.moduloapi.config.IDiscUtil;
import fr.aytronn.moduloapi.utils.threads.ZakaryThread;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiscUtil implements IDiscUtil {

    private final Object2ObjectMap<String, Lock> locks;

    public DiscUtil() {
        this.locks = new Object2ObjectOpenHashMap<>();
    }

    @Override
    public byte[] readBytes(File file) {
        final CompletableFuture<byte[]> completableFuture = CompletableFuture.supplyAsync(() -> {
            final int length = (int) file.length();
            final var output = new byte[length];
            try (final var in = new FastBufferedInputStream(new FileInputStream(file))) {
                var offset = 0;
                while (offset < length) {
                    offset += in.read(output, offset, (length - offset));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        });
        return completableFuture.join();
    }

    @Override
    public void writeBytes(File file, byte[] bytes) {
        final Runnable runnable = () -> {
            try (final var out = new FastBufferedOutputStream(new FileOutputStream(file))) {
                out.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        ZakaryThread.FILE_EXECUTOR.execute(runnable);
    }

    @Override
    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    public void writeCatch(final File file, final String content) {
        final String name = file.getName();
        // Create lock for each file if there isn't already one.
        final var lock = this.locks.computeIfAbsent(name, function -> {
            final var rwl = new ReentrantReadWriteLock();
            return rwl.writeLock();
        });

        final Runnable runnable = () -> {
            lock.lock();
            try {
                file.createNewFile();
                Files.write(content, file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };
        ZakaryThread.FILE_EXECUTOR.execute(runnable);
    }
}
