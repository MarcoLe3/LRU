package org.example;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    static void main() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        LRUCustomCache cache = new LRUCustomCache(24, 4);
        Random random = new Random();
        long start = System.currentTimeMillis();

        Future<?> threadOne = executor.submit(() -> {
            for (int i = 0; i < 20000; i++) {
                cache.put(random.nextInt(), random.nextInt());
            }
        });

        Future<?> threadTwo = executor.submit(() -> {
            for (int i = 0; i < 20000; i++) {
                cache.put(random.nextInt(), random.nextInt());
            }
            cache.printLinkedList();
        });

        threadOne.get();
        threadTwo.get();

        long end = System.currentTimeMillis();
        System.out.printf("Total wall time: %d %n", (end - start));
        executor.shutdown();
    }
}
