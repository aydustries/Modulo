package fr.aytronn.moduloapi.utils;

import java.util.Timer;

public class TimerUtil {

    private static final Timer timer = new Timer();

    public static Timer getTimer() {
        return timer;
    }
}
