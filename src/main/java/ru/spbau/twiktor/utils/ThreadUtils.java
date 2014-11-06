package ru.spbau.twiktor.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by serce on 11/6/14.
 */
public class ThreadUtils {
    public static void sleep(int fromMillis, int toMillis) {
        try {
            Thread.sleep(fromMillis + ThreadLocalRandom.current().nextInt(toMillis - fromMillis));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
