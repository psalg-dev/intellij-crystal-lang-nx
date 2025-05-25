package org.intellij.crystal;

import com.intellij.remoterobot.stepsProcessing.StepLogger;
import com.intellij.remoterobot.stepsProcessing.StepWorker;

public class StepsLogger {
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            StepWorker.registerProcessor(new StepLogger());
            initialized = true;
        }
    }
}