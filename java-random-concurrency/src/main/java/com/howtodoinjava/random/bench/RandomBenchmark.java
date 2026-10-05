package com.howtodoinjava.random.bench;

import java.security.SecureRandom;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * One dice roll (nextInt(1, 7)) per operation. Run with -t 1, -t 4 and -t 8.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class RandomBenchmark {

  /** One instance for all benchmark threads. */
  @State(Scope.Benchmark)
  public static class Shared {
    final Random random = new Random();
    final SecureRandom secureRandom = new SecureRandom();
  }

  /** One instance per benchmark thread. */
  @State(Scope.Thread)
  public static class PerThread {
    final SplittableRandom splittable = new SplittableRandom();
    final RandomGenerator lxm = RandomGeneratorFactory.of("L64X128MixRandom").create();
  }

  @Benchmark
  public int sharedRandom(Shared s) {
    return s.random.nextInt(1, 7);
  }

  @Benchmark
  public int mathRandom() {
    return 1 + (int) (Math.random() * 6);
  }

  @Benchmark
  public int threadLocalRandom() {
    return ThreadLocalRandom.current().nextInt(1, 7);
  }

  @Benchmark
  public int perThreadSplittableRandom(PerThread p) {
    return p.splittable.nextInt(1, 7);
  }

  @Benchmark
  public int perThreadL64X128MixRandom(PerThread p) {
    return p.lxm.nextInt(1, 7);
  }

  @Benchmark
  public int sharedSecureRandom(Shared s) {
    return s.secureRandom.nextInt(1, 7);
  }

  @Benchmark
  public int newRandomPerCall() {
    return new Random().nextInt(1, 7);
  }
}
