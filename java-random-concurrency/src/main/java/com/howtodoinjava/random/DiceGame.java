package com.howtodoinjava.random;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.random.RandomGenerator;

/**
 * Dice game: every player rolls a die many times on its own thread, and the total wins.
 */
public final class DiceGame {

  private DiceGame() {
  }

  public static int roll(RandomGenerator rng) {
    return rng.nextInt(1, 7);
  }

  static int total(RandomGenerator rng, int rolls) {
    int sum = 0;
    for (int i = 0; i < rolls; i++) {
      sum += roll(rng);
    }
    return sum;
  }

  /**
   * Reproducible: one generator per player, split from a seeded root in a fixed order
   * on the calling thread, before any task starts.
   */
  public static Map<String, Integer> playWithSplits(List<String> players, long seed, int rolls, int threads)
      throws Exception {
    SplittableRandom root = new SplittableRandom(seed);
    Map<String, SplittableRandom> perPlayer = new LinkedHashMap<>();
    for (String player : players) {
      perPlayer.put(player, root.split());           // split in a fixed order
    }
    return play(perPlayer, rolls, threads);
  }

  /**
   * Not reproducible: all players share one seeded Random, so each player gets whatever
   * values its thread happens to read next.
   */
  public static Map<String, Integer> playWithSharedRandom(List<String> players, long seed, int rolls, int threads)
      throws Exception {
    Random shared = new Random(seed);
    Map<String, RandomGenerator> perPlayer = new LinkedHashMap<>();
    for (String player : players) {
      perPlayer.put(player, shared);
    }
    return play(perPlayer, rolls, threads);
  }

  private static Map<String, Integer> play(Map<String, ? extends RandomGenerator> perPlayer, int rolls, int threads)
      throws Exception {
    Map<String, Future<Integer>> futures = new LinkedHashMap<>();
    try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
      perPlayer.forEach((player, rng) -> futures.put(player, pool.submit(() -> total(rng, rolls))));
      Map<String, Integer> totals = new LinkedHashMap<>();
      for (var e : futures.entrySet()) {
        totals.put(e.getKey(), e.getValue().get());
      }
      return totals;
    }
  }
}
