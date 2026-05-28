## Benchmarks:

| Benchmark | Mode | Cnt | Score | Error | Units |
|-----------|------|-----|-------|-------|-------|
| StampedLock segmentedCacheGet | thrpt | 25 | 49,451.117 | ± 1,250.501 | ops/ms |
| StampedLock segmentedCachePut | thrpt | 25 | 10,915.205 | ± 142.089 | ops/ms |
| Baseline segmentedCacheGet | thrpt | 25 | 12,318.546 | ± 884.087 | ops/ms |
| Baseline segmentedCachePut | thrpt | 25 | 7,266.876 | ± 230.859 | ops/ms |

## Results: 

| Operation | Baseline | StampedLock | Improvement |
|-----------|----------|-------------|-------------|
| Get | 12,318 ops/ms | 49,451 ops/ms | **4x faster** |
| Put | 7,266 ops/ms | 10,915 ops/ms | **1.5x faster** |
