package de.jan;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static de.jan.Main.SC2_LOCATION;
import static de.jan.Main.THREADS;
import static de.jan.Main.starcrafts;

public class SCutil {

    public static void killStarcraft() {
        for (int i = 0; i < starcrafts.length; i++) {
            starcrafts[i].destroyForcibly();
        }
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void launchStarcraft() {
        for (int i = 0; i < THREADS; i++) {
            launchStarcraft(i);
        }
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void launchStarcraft(int i) {
        try {
            starcrafts[i] = new ProcessBuilder(SC2_LOCATION,
                    "-listen", "127.0.0.1", "-port", Integer.toString(8167 + i)).start();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void relaunchStarcraft(int i) {
        try {
            starcrafts[i].destroyForcibly();
            TimeUnit.SECONDS.sleep(1);
            launchStarcraft(i);
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void relaunchStarcraft() {
        try {
            for (int i = 0; i < starcrafts.length; i++) {
                starcrafts[i].destroyForcibly();
            }
            TimeUnit.SECONDS.sleep(1);
            launchStarcraft();
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
