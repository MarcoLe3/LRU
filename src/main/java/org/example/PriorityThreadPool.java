package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityThreadPool {
    private boolean                                 isStopped = false;
    private PriorityBlockingQueue<Runnable>         taskQueue = null;
    private final List<PoolThreadRunnable>          listPoolThreadRunnables = new ArrayList<>();

    public PriorityThreadPool(int noOfThreads, int maxNoOfThreads) {
        this.taskQueue = new PriorityBlockingQueue(maxNoOfThreads);

        for (int i = 0; i < noOfThreads; i++) {
            PoolThreadRunnable poolThreadRunnable = new PoolThreadRunnable(taskQueue);
            listPoolThreadRunnables.add(poolThreadRunnable);
        }

        for (Runnable poolThreadRunnable : listPoolThreadRunnables) {
            new Thread(poolThreadRunnable).start();
        }
    }

    public synchronized void execute(Runnable task) throws Exception {
        if (this.isStopped) { throw new IllegalStateException(); }
        this.taskQueue.offer(task);
    }

    public synchronized void shutdown() {
        this.isStopped = true;
        for (PoolThreadRunnable poolThreadRunnable : listPoolThreadRunnables) {
            poolThreadRunnable.stop();
        }
    }

    public synchronized void waitUntilAllTasksDone() throws InterruptedException {
        while (!this.taskQueue.isEmpty()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println();
            }
        }
    }
}
