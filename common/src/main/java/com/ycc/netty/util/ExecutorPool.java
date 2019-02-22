package com.ycc.netty.util;

import java.util.concurrent.*;

/**
 * @author :MysticalYcc
 * @date :10:45 2019/2/22
 */
public enum ExecutorPool {
    /**
     * 线程池单例对象
     */
    instance;
    private static Executor executor;

    static {
        executor = new ThreadPoolExecutor(
                4,
                10,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new RejectedHandler(100));
    }


    public static Executor getInstance() {
        return executor;
    }


}


class RejectedHandler implements RejectedExecutionHandler {
    private int rejectQueueSize = 100;

    RejectedHandler(int rejectQueueSize) {
        this.rejectQueueSize = rejectQueueSize;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        BlockingQueue rejectedQueue = new ArrayBlockingQueue(rejectQueueSize);
        if (rejectedQueue.size() == rejectQueueSize) {
            System.out.println("rejectQueue is full! the after runnable will discard");
        } else {
            rejectedQueue.add(r);
        }
    }
}
