package com.kanwaljeetsm.pomodoro;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
