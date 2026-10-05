package com.howtodoinjava.random;

import java.util.random.RandomGenerator;

/**
 * Raffle ticket draw: picks 6 distinct numbers from 1 to 49.
 * The generator is passed in, so the caller decides which one (and which seed) to use.
 */
public final class TicketDraw {

  public static final int NUMBERS = 6;
  public static final int MAX = 49;

  private TicketDraw() {
  }

  public static int[] draw(RandomGenerator rng) {
    return rng.ints(1, MAX + 1)
        .distinct()
        .limit(NUMBERS)
        .sorted()
        .toArray();
  }
}
