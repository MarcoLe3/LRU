package org.example;

import java.util.concurrent.BlockingQueue;

public class PoolThreadRunnable implements Runnable {
    private Thread          thread;
    private boolean         isStopped = false;
    private BlockingQueue   taskQueue = null;

    public PoolThreadRunnable(BlockingQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void run(){
        this.thread = Thread.currentThread();
        while(!this.isStopped){
            try {
                Runnable task = (Runnable)this.taskQueue.take();
                task.run();
            } catch (Exception e) {
            }
        }
    }

    public void stop() {
        this.isStopped = true;
        this.thread.interrupt();
    }

    public synchronized boolean isStopped() {
        return this.isStopped;
    }
}
