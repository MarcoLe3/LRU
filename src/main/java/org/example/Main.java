package org.example;

import java.util.Random;

public class Main {
    static void main() throws Exception {
        DefaultThreadPool defaultThreadPool = new DefaultThreadPool(3, 10);
        StampedLockLRUCache cache = new StampedLockLRUCache(24, 4);
        Random random = new Random();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++){
            defaultThreadPool.execute(() -> {
                for (int j = 0; j < 50000; j++) {
                    cache.put(random.nextInt(), random.nextInt());
                }
            });
        }

        long end = System.currentTimeMillis();
        System.out.printf("Total wait time: %d %n", (end - start));
        defaultThreadPool.waitUntilAllTasksDone();
        defaultThreadPool.shutdown();
    }
}
