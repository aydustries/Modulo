package fr.aytronn.moduloapi.api.config;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author HookWoods
 */
public interface IDiscUtil {
    /**
     * Read Bytes
     *
     * @param file file
     * @return Bytes
     */
    byte[] readBytes(File file);

    /**
     * Allow to writes Bytes
     *
     * @param file Files
     * @param bytes Bytes
     */
    void writeBytes(File file, byte[] bytes);

    /**
     * Allow to write
     *
     * @param file File
     * @param content Content
     */
    default void write(File file, String content) {
        writeBytes(file, utf8(content));
    }

    /**
     * Allow to read
     *
     * @param file File
     * @return String
     */
    default String read(File file) {
        return utf8(readBytes(file));
    }

    /**
     * Allow to witre catch
     *
     * @param file File
     * @param content Content
     */
    void writeCatch(File file, String content);

    /**
     * Allow to read catch
     *
     * @param file File
     * @return The string
     */
    default String readCatch(File file) {
        return read(file);
    }

    /**
     * Utf8
     *
     * @param string String
     * @return Bytes
     */
    default byte[] utf8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Utf8
     *
     * @param bytes Bytes
     * @return String
     */
    default String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
