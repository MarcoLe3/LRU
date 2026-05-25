package com.jenkov;

import org.example.LRUCustomCache;
import org.example.DefaultThreadPool;
import org.example.PriorityThreadPool;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MyBenchmark {
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MINUTES)
    public void defaultBenchmark() throws Exception {
        DefaultThreadPool defaultThreadPool = new DefaultThreadPool(3, 10);
        LRUCustomCache cache = new LRUCustomCache(24, 4);
        Random random = new Random();

        for (int i = 0; i < 10; i++){
            defaultThreadPool.execute(() -> {
                for (int j = 0; j < 50000; j++) {
                    cache.put(random.nextInt(), random.nextInt());
                }
            });
        }
        defaultThreadPool.waitUntilAllTasksDone();
        defaultThreadPool.shutdown();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MINUTES)
    public void priorityBenchmark() throws Exception {
        PriorityThreadPool priorityThreadPool = new PriorityThreadPool(3, 10);
        LRUCustomCache cache = new LRUCustomCache(24, 4);
        Random random = new Random();

        for (int i = 0; i < 10; i++){
            priorityThreadPool.execute(() -> {
                for (int j = 0; j < 50000; j++) {
                    cache.put(random.nextInt(), random.nextInt());
                }
            });
        }
        priorityThreadPool.waitUntilAllTasksDone();
        priorityThreadPool.shutdown();
    }
}
