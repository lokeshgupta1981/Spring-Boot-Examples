package com.howtodoinjava.random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Two common mistakes with random generators in concurrent code.
 */
public final class CommonBugs {

  // Bug 1: the ThreadLocalRandom of the main thread is stored in a field
  private static final ThreadLocalRandom TLR_FIELD = ThreadLocalRandom.current();

  private CommonBugs() {
  }

  /** Rolls dice on a new thread that uses the stored field instead of current(). */
  public static List<Integer> rollWithStoredThreadLocalRandom(int count) throws InterruptedException {
    return rollOnNewThread(() -> TLR_FIELD, count);
  }

  /** The correct form: call current() on the thread that uses it. */
  public static List<Integer> rollWithCurrentThreadLocalRandom(int count) throws InterruptedException {
    return rollOnNewThread(ThreadLocalRandom::current, count);
  }

  private static List<Integer> rollOnNewThread(Supplier<ThreadLocalRandom> rng, int count)
      throws InterruptedException {
    List<Integer> rolls = new ArrayList<>();
    Thread player = new Thread(() -> {
      for (int i = 0; i < count; i++) {
        rolls.add(DiceGame.roll(rng.get()));
      }
    });
    player.start();
    player.join();
    return rolls;
  }

  // Bug 2: a new Random per call, seeded with the clock
  public static int[] drawWithClockSeed() {
    return TicketDraw.draw(new Random(System.currentTimeMillis()));
  }

  public static List<String> drawManyWithClockSeed(int count) {
    List<String> tickets = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      tickets.add(Arrays.toString(drawWithClockSeed()));
    }
    return tickets;
  }

  /** Prints the stored-field rolls; run it twice to see the same "random" rolls. */
  public static void main(String[] args) throws InterruptedException {
    System.out.println(rollWithStoredThreadLocalRandom(10));
  }
}
