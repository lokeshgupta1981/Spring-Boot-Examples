package com.howtodoinjava.random;

import java.security.DrbgParameters;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.JumpableGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.IntStream;

import static java.security.DrbgParameters.Capability.PR_AND_RESEED;

/**
 * Runs every example of the article and prints the results.
 */
public class RandomDemo {

  static final List<String> PLAYERS = List.of("Alex", "Ben", "Chen", "Dana");

  public static void main(String[] args) throws Exception {
    System.out.println("== 1. java.util.Random with a seed");
    System.out.println("ticket Random(42) = " + Arrays.toString(TicketDraw.draw(new Random(42))));
    System.out.println("ticket Random(42) again = " + Arrays.toString(TicketDraw.draw(new Random(42))));

    System.out.println("== 2. ThreadLocalRandom");
    System.out.println("roll = " + DiceGame.roll(ThreadLocalRandom.current()));
    try {
      ThreadLocalRandom.current().setSeed(42);
    } catch (UnsupportedOperationException e) {
      System.out.println("setSeed -> " + e);
    }

    System.out.println("distinct values from 10000 virtual threads = " + virtualThreadDistinctValues(10_000));

    System.out.println("== 3. SplittableRandom");
    SplittableRandom root = new SplittableRandom(42);
    SplittableRandom alex = root.split();
    SplittableRandom ben = root.split();
    System.out.println("alex rolls = " + rolls(alex, 5));
    System.out.println("ben rolls = " + rolls(ben, 5));
    SplittableRandom root2 = new SplittableRandom(42);
    System.out.println("alex rolls again = " + rolls(root2.split(), 5));

    System.out.println("== 4. RandomGenerator API");
    System.out.println("getDefault() = " + RandomGenerator.getDefault().getClass().getSimpleName());
    RandomGenerator lxm = RandomGenerator.of("L64X128MixRandom");
    System.out.println("of(L64X128MixRandom) = " + lxm.getClass().getSimpleName());
    RandomGenerator seeded = RandomGeneratorFactory.of("L64X128MixRandom").create(42);
    System.out.println("ticket L64X128MixRandom(42) = " + Arrays.toString(TicketDraw.draw(seeded)));
    System.out.println("name | group | splittable | jumpable | streamable | stateBits");
    RandomGeneratorFactory.all()
        .sorted(Comparator.comparing(RandomGeneratorFactory::name))
        .forEach(f -> System.out.printf("%s | %s | %s | %s | %s | %d%n",
            f.name(), f.group(), f.isSplittable(), f.isJumpable(), f.isStreamable(), f.stateBits()));
    RandomGenerator splittableOnly = RandomGeneratorFactory.all()
        .filter(RandomGeneratorFactory::isSplittable)
        .filter(f -> f.stateBits() >= 128)
        .min(Comparator.comparingInt(RandomGeneratorFactory::stateBits))
        .orElseThrow()
        .create();
    System.out.println("smallest splittable with >= 128 state bits = " + splittableOnly.getClass().getSimpleName());

    System.out.println("== 5. splits() and jumps()");
    SplittableGenerator lxmRoot = (SplittableGenerator) RandomGeneratorFactory.of("L64X128MixRandom").create(42);
    List<SplittableGenerator> lxmPlayers = lxmRoot.splits(4).toList();
    System.out.println("splits(4) = " + lxmPlayers.size() + " generators, first rolls = "
        + lxmPlayers.stream().map(g -> DiceGame.roll(g)).toList());
    JumpableGenerator xoshiro = (JumpableGenerator) RandomGeneratorFactory.of("Xoshiro256PlusPlus").create(42);
    List<RandomGenerator> lanes = xoshiro.jumps(3).toList();
    System.out.println("jumps(3) = " + lanes.size() + " generators, first rolls = "
        + lanes.stream().map(g -> DiceGame.roll(g)).toList());

    System.out.println("== 6. Dice game, seed 42, 1000 rolls per player");
    System.out.println("splits, 1 thread  = " + DiceGame.playWithSplits(PLAYERS, 42, 1000, 1));
    System.out.println("splits, 4 threads = " + DiceGame.playWithSplits(PLAYERS, 42, 1000, 4));
    System.out.println("shared Random(42), 4 threads, run 1 = " + DiceGame.playWithSharedRandom(PLAYERS, 42, 1000, 4));
    System.out.println("shared Random(42), 4 threads, run 2 = " + DiceGame.playWithSharedRandom(PLAYERS, 42, 1000, 4));

    System.out.println("== 7. Parallel streams");
    long sequential = new SplittableRandom(42).ints(1_000_000, 1, 7).asLongStream().sum();
    System.out.println("sequential sum = " + sequential);
    System.out.println("parallel sums in 20 runs = " + parallelSums(42, 20));
    for (int run = 1; run <= 2; run++) {
      System.out.println("pre-split parallel sum, run " + run + " = " + preSplitSum(42, 1_000, 1_000));
    }

    System.out.println("== 8. SecureRandom");
    SecureRandom secure = new SecureRandom();
    System.out.println("new SecureRandom() = " + secure.getAlgorithm());
    System.out.println("ticket = " + Arrays.toString(TicketDraw.draw(secure)));
    long start = System.nanoTime();
    SecureRandom strong = SecureRandom.getInstanceStrong();
    strong.nextInt();
    System.out.println("getInstanceStrong() = " + strong.getAlgorithm()
        + ", first nextInt took " + (System.nanoTime() - start) / 1_000 + " us");
    SecureRandom drbg = SecureRandom.getInstance("DRBG",
        DrbgParameters.instantiation(256, PR_AND_RESEED, "raffle".getBytes()));
    System.out.println("DRBG = " + drbg.getAlgorithm() + ", " + drbg.getParameters());

    System.out.println("== 9. Common bugs");
    System.out.println("stored ThreadLocalRandom field, new thread = "
        + CommonBugs.rollWithStoredThreadLocalRandom(10));
    System.out.println("current() on the new thread = "
        + CommonBugs.rollWithCurrentThreadLocalRandom(10));
    System.out.println("new Random(currentTimeMillis()) x3 = " + CommonBugs.drawManyWithClockSeed(3));
  }

  /** Runs the same seeded parallel stream several times on a 4-thread pool and collects the distinct sums. */
  static Set<Long> parallelSums(long seed, int runs) throws Exception {
    Set<Long> sums = new TreeSet<>();
    try (ForkJoinPool pool = new ForkJoinPool(4)) {
      for (int run = 0; run < runs; run++) {
        sums.add(pool.submit(() ->
            new SplittableRandom(seed).ints(1_000_000, 1, 7).parallel().asLongStream().sum()).get());
      }
    }
    return sums;
  }

  /** Every virtual thread gets its own ThreadLocalRandom seed. */
  static int virtualThreadDistinctValues(int threads) {
    Set<Long> values = ConcurrentHashMap.newKeySet();
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < threads; i++) {
        executor.submit(() -> values.add(ThreadLocalRandom.current().nextLong()));
      }
    }
    return values.size();
  }

  static List<Integer> rolls(RandomGenerator rng, int count) {
    return IntStream.range(0, count).map(i -> DiceGame.roll(rng)).boxed().toList();
  }

  /** Reproducible parallel work: split one generator per chunk first, then run the chunks in parallel. */
  static long preSplitSum(long seed, int chunks, int rollsPerChunk) {
    SplittableRandom root = new SplittableRandom(seed);
    List<SplittableRandom> perChunk = IntStream.range(0, chunks).mapToObj(i -> root.split()).toList();
    return IntStream.range(0, chunks).parallel()
        .mapToLong(i -> DiceGame.total(perChunk.get(i), rollsPerChunk))
        .sum();
  }
}
