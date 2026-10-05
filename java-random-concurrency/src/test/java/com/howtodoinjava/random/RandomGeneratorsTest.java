package com.howtodoinjava.random;

import java.io.File;
import java.security.DrbgParameters;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.JumpableGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static java.security.DrbgParameters.Capability.PR_AND_RESEED;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomGeneratorsTest {

  static final List<String> PLAYERS = RandomDemo.PLAYERS;

  @Test
  void seededRandomDrawsTheSameTicket() {
    int[] expected = {20, 26, 30, 32, 34, 35};
    assertArrayEquals(expected, TicketDraw.draw(new Random(42)));
    assertArrayEquals(expected, TicketDraw.draw(new Random(42)));
  }

  @Test
  void ticketHasSixDistinctSortedNumbers() {
    int[] ticket = TicketDraw.draw(ThreadLocalRandom.current());
    assertEquals(6, ticket.length);
    for (int i = 0; i < ticket.length; i++) {
      assertTrue(ticket[i] >= 1 && ticket[i] <= 49);
      if (i > 0) {
        assertTrue(ticket[i] > ticket[i - 1]);
      }
    }
  }

  @Test
  void threadLocalRandomCannotBeSeeded() {
    assertThrows(UnsupportedOperationException.class, () -> ThreadLocalRandom.current().setSeed(42));
  }

  @Test
  void eachVirtualThreadHasItsOwnThreadLocalRandom() {
    assertEquals(10_000, RandomDemo.virtualThreadDistinctValues(10_000));
  }

  @Test
  void storedThreadLocalRandomStillRollsValidValues() throws Exception {
    List<Integer> rolls = CommonBugs.rollWithStoredThreadLocalRandom(10);
    assertEquals(10, rolls.size());
    assertTrue(rolls.stream().allMatch(r -> r >= 1 && r <= 6));
  }

  @Test
  void splitGeneratorsAreIndependentAndReproducible() {
    SplittableRandom root = new SplittableRandom(42);
    SplittableRandom alex = root.split();
    SplittableRandom ben = root.split();
    assertEquals(List.of(4, 3, 4, 4, 3), RandomDemo.rolls(alex, 5));
    assertEquals(List.of(5, 2, 3, 3, 4), RandomDemo.rolls(ben, 5));
    assertEquals(List.of(4, 3, 4, 4, 3), RandomDemo.rolls(new SplittableRandom(42).split(), 5));
  }

  @Test
  void randomGeneratorApi() {
    assertEquals("L32X64MixRandom", RandomGenerator.getDefault().getClass().getSimpleName());
    assertEquals("L64X128MixRandom", RandomGenerator.of("L64X128MixRandom").getClass().getSimpleName());
    assertArrayEquals(new int[] {17, 20, 22, 27, 31, 32},
        TicketDraw.draw(RandomGeneratorFactory.of("L64X128MixRandom").create(42)));
  }

  @Test
  void factoryProperties() {
    RandomGeneratorFactory<RandomGenerator> lxm = RandomGeneratorFactory.of("L64X128MixRandom");
    assertEquals("LXM", lxm.group());
    assertTrue(lxm.isSplittable());
    assertFalse(lxm.isJumpable());
    assertTrue(lxm.isStreamable());
    assertEquals(192, lxm.stateBits());

    RandomGeneratorFactory<RandomGenerator> xoshiro = RandomGeneratorFactory.of("Xoshiro256PlusPlus");
    assertTrue(xoshiro.isJumpable());
    assertFalse(xoshiro.isSplittable());

    RandomGeneratorFactory<RandomGenerator> legacy = RandomGeneratorFactory.of("Random");
    assertFalse(legacy.isSplittable());
    assertEquals(48, legacy.stateBits());

    assertEquals(13, RandomGeneratorFactory.all().count());
  }

  @Test
  void splitsAndJumps() {
    SplittableGenerator lxmRoot = (SplittableGenerator) RandomGeneratorFactory.of("L64X128MixRandom").create(42);
    assertEquals(List.of(5, 6, 1, 5), lxmRoot.splits(4).map(DiceGame::roll).toList());

    JumpableGenerator xoshiro = (JumpableGenerator) RandomGeneratorFactory.of("Xoshiro256PlusPlus").create(42);
    assertEquals(List.of(1, 6, 5), xoshiro.jumps(3).map(DiceGame::roll).toList());
  }

  @Test
  void diceGameWithSplitsIsReproducibleForAnyThreadCount() throws Exception {
    Map<String, Integer> expected = Map.of("Alex", 3473, "Ben", 3584, "Chen", 3421, "Dana", 3537);
    for (int threads : new int[] {1, 4, 8}) {
      assertEquals(expected, DiceGame.playWithSplits(PLAYERS, 42, 1000, threads));
    }
  }

  @Test
  void diceGameWithSharedRandomKeepsOnlyTheGrandTotal() throws Exception {
    // The 4000 rolls come from one seeded sequence, but which player gets which rolls depends on timing
    for (int run = 0; run < 3; run++) {
      Map<String, Integer> totals = DiceGame.playWithSharedRandom(PLAYERS, 42, 1000, 4);
      assertEquals(13914, totals.values().stream().mapToInt(Integer::intValue).sum());
    }
  }

  @Test
  void sequentialAndPreSplitSumsAreReproducible() {
    assertEquals(3500266, new SplittableRandom(42).ints(1_000_000, 1, 7).asLongStream().sum());
    assertEquals(3499222, RandomDemo.preSplitSum(42, 1_000, 1_000));
    assertEquals(3499222, RandomDemo.preSplitSum(42, 1_000, 1_000));
  }

  @Test
  @EnabledOnOs(OS.LINUX)
  void secureRandomAlgorithmsOnLinux() throws Exception {
    assertEquals("NativePRNG", new SecureRandom().getAlgorithm());
    assertEquals("NativePRNGBlocking", SecureRandom.getInstanceStrong().getAlgorithm());
    SecureRandom drbg = SecureRandom.getInstance("DRBG",
        DrbgParameters.instantiation(256, PR_AND_RESEED, "raffle".getBytes()));
    assertTrue(drbg.getParameters().toString().startsWith("256,pr_and_reseed"));
  }

  @Test
  void storedThreadLocalRandomRepeatsAcrossJvmRuns() throws Exception {
    String first = runCommonBugsInNewJvm();
    String second = runCommonBugsInNewJvm();
    assertEquals(first, second);
  }

  @Test
  void clockSeededRandomRepeatsTickets() {
    List<String> tickets = CommonBugs.drawManyWithClockSeed(3);
    assertTrue(new HashSet<>(tickets).size() < 3);
  }

  private static String runCommonBugsInNewJvm() throws Exception {
    String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    Process process = new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"),
        CommonBugs.class.getName()).start();
    String output = new String(process.getInputStream().readAllBytes()).trim();
    assertEquals(0, process.waitFor());
    return output;
  }
}
