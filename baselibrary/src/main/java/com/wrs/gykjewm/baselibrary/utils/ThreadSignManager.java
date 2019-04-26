package com.wrs.gykjewm.baselibrary.utils;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程管理工具，用于处理一步任务
 * @author
 *
 */
public class ThreadSignManager {
    private final int THREAD_FIXED =8;
    private static ThreadSignManager sInstance = null;
    private ExecutorService mExecutorService;

    private ThreadSignManager() {

        //单例的线程池
        this.mExecutorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized ThreadSignManager getInstance() {
        if (sInstance == null) {
            sInstance = new ThreadSignManager();
        }
        return sInstance;
    }

    public void submit(Runnable task) {
        this.mExecutorService.submit(task);
    }

    public Future<Integer> submit(Callable<Integer> task) {
        return this.mExecutorService.submit(task);
    }

    public void shutdown() {
        if (!this.mExecutorService.isShutdown())
            this.mExecutorService.shutdownNow();
    }
}
