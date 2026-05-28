package com.jenkov;

import org.example.StampedLockLRUCache;
import org.example.DefaultThreadPool;
import org.example.PriorityThreadPool;

import org.example.StandardLRUCache;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MyBenchmark {
    private StampedLockLRUCache stampedLockLRUCache;
    private StandardLRUCache standardLRUCache;
    private DefaultThreadPool defaultThreadPool;
    private PriorityThreadPool priorityThreadPool;
    private Random random;


    @Setup(Level.Trial)
    public void setup() {
        stampedLockLRUCache = new StampedLockLRUCache(24, 4);
        standardLRUCache = new StandardLRUCache(24, 4);
        defaultThreadPool = new DefaultThreadPool(3, 10);
        priorityThreadPool = new PriorityThreadPool(3, 10);
        random = new Random();
    }

    @TearDown(Level.Trial)
    public void teardown() {
        defaultThreadPool.shutdown();
        priorityThreadPool.shutdown();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Threads(4)
    public void segmentedCachePut() {
        standardLRUCache.put(random.nextInt(1000), random.nextInt());
    }

//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    @Threads(4)
//    public void segmentedCacheGet() {
//        standardLRUCache.get(random.nextInt(1000));
//    }
}
