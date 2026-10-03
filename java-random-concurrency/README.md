# Java Random Number Generators in Multithreaded Code

Source code for the article [Java Random Number Generators in Multithreaded Code](https://howtodoinjava.com/?p=40476).

A raffle ticket draw and a dice game run by many threads, with `java.util.Random`, `ThreadLocalRandom`,
`SplittableRandom`, the `java.util.random.RandomGenerator` API (`L64X128MixRandom`, `Xoshiro256PlusPlus`)
and `SecureRandom`, plus a JMH benchmark.

## Versions

- Java 25
- JMH 1.37
- JUnit 6.1.3
- Maven 3.9 (maven-compiler-plugin 3.16.0, maven-surefire-plugin 3.6.0, maven-shade-plugin 3.6.2, exec-maven-plugin 3.6.4)

## Run

```bash
mvn -q compile exec:java                                   # prints every result of the article
mvn test                                                   # 15 tests
mvn package -DskipTests                                    # builds target/benchmarks.jar
java -jar target/benchmarks.jar -t 1                       # JMH with 1 thread (use -t 4 and -t 8 too)
java -cp target/classes com.howtodoinjava.random.CommonBugs   # run twice: same "random" rolls
```

## Files

| File | What it shows |
|---|---|
| `TicketDraw.java` | Draws 6 distinct numbers from 1 to 49 with any `RandomGenerator` |
| `DiceGame.java` | Dice rolls per player on a thread pool: split generators (reproducible) vs one shared `Random` |
| `CommonBugs.java` | `ThreadLocalRandom` stored in a field, and a clock-seeded `new Random()` per call |
| `RandomDemo.java` | Runs every example and prints the results |
| `bench/RandomBenchmark.java` | JMH benchmark: shared `Random`, `Math.random()`, `ThreadLocalRandom`, per-thread `SplittableRandom` and `L64X128MixRandom`, shared `SecureRandom`, `new Random()` per call |
| `RandomGeneratorsTest.java` | JUnit tests for every result in the article |
