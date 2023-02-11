package fr.aytronn.moduloapi.config;

import java.io.File;
import java.nio.charset.StandardCharsets;

public interface IDiscUtil {
    /**
     * Read Bytes
     *
     * @param file file
     * @return Bytes
     */
    public byte[] readBytes(File file);

    /**
     * Allow to writes Bytes
     *
     * @param file Files
     * @param bytes Bytes
     */
    public void writeBytes(File file, byte[] bytes);

    /**
     * Allow to write
     *
     * @param file File
     * @param content Content
     */
    public default void write(File file, String content) {
        writeBytes(file, utf8(content));
    }

    /**
     * Allow to read
     *
     * @param file File
     * @return String
     */
    public default String read(File file) {
        return utf8(readBytes(file));
    }

    /**
     * Allow to witre catch
     *
     * @param file File
     * @param content Content
     */
    public void writeCatch(File file, String content);

    /**
     * Allow to read catch
     *
     * @param file File
     * @return The string
     */
    public default String readCatch(File file) {
        return read(file);
    }

    /**
     * Utf8
     *
     * @param string String
     * @return Bytes
     */
    public default byte[] utf8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Utf8
     *
     * @param bytes Bytes
     * @return String
     */
    public default String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
