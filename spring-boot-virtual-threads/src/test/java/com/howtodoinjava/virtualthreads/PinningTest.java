package com.howtodoinjava.virtualthreads;

import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Records jdk.VirtualThreadPinned events with JDK Flight Recorder while a virtual thread blocks
 * in different places. Since JDK 24 (JEP 491), blocking inside synchronized no longer pins.
 */
class PinningTest {

  private static final Duration BLOCK = Duration.ofMillis(50);

  @Test
  void blockingInsideSynchronizedDoesNotPin() throws Exception {
    Object lock = new Object();
    List<RecordedEvent> events = recordPinnedEvents(() -> {
      synchronized (lock) {
        pause();
      }
    });
    assertThat(events).isEmpty();
  }

  @Test
  void blockingWhileHoldingReentrantLockDoesNotPin() throws Exception {
    ReentrantLock lock = new ReentrantLock();
    List<RecordedEvent> events = recordPinnedEvents(() -> {
      lock.lock();
      try {
        pause();
      } finally {
        lock.unlock();
      }
    });
    assertThat(events).isEmpty();
  }

  @Test
  void blockingInsideNativeCallbackPins() throws Exception {
    List<RecordedEvent> events = recordPinnedEvents(PinningTest::sortWithNativeQsort);

    assertThat(events).hasSize(1);
    RecordedEvent event = events.getFirst();
    assertThat(event.getDuration()).isGreaterThanOrEqualTo(BLOCK);
    assertThat(event.getString("pinnedReason")).isEqualTo("Native or VM frame on stack");
    System.out.println(event);
  }

  @Test
  void blockingInsideClassInitializerPins() throws Exception {
    List<RecordedEvent> events = recordPinnedEvents(() -> SlowInit.VALUE.length());
    System.out.println("class initializer events: " + events.size());
    events.forEach(e -> System.out.println("reason: " + e.getString("pinnedReason")));
    assertThat(events).hasSize(1);
  }

  static class SlowInit {
    static final String VALUE;

    static {
      pause();
      VALUE = "ready";
    }
  }

  // Runs the action on a new virtual thread and returns the pinned events JFR recorded
  private static List<RecordedEvent> recordPinnedEvents(Runnable action) throws Exception {
    Path file = Files.createTempFile("pinning", ".jfr");
    try (Recording recording = new Recording()) {
      recording.enable("jdk.VirtualThreadPinned").withThreshold(Duration.ofMillis(20));
      recording.start();
      Thread.ofVirtual().start(action).join();
      recording.stop();
      recording.dump(file);
    }
    try {
      return RecordingFile.readAllEvents(file).stream()
          .filter(e -> e.getEventType().getName().equals("jdk.VirtualThreadPinned"))
          .toList();
    } finally {
      Files.deleteIfExists(file);
    }
  }

  // qsort from the C library calls back into Java to compare two ints
  private static void sortWithNativeQsort() {
    try {
      Linker linker = Linker.nativeLinker();
      MethodHandle qsort = linker.downcallHandle(
          linker.defaultLookup().find("qsort").orElseThrow(),
          FunctionDescriptor.ofVoid(ADDRESS, JAVA_LONG, JAVA_LONG, ADDRESS));
      MethodHandle compare = MethodHandles.lookup().findStatic(PinningTest.class, "compare",
          MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class));
      FunctionDescriptor compareDescriptor = FunctionDescriptor.of(JAVA_INT,
          ADDRESS.withTargetLayout(JAVA_INT), ADDRESS.withTargetLayout(JAVA_INT));

      try (Arena arena = Arena.ofConfined()) {
        MemorySegment callback = linker.upcallStub(compare, compareDescriptor, arena);
        MemorySegment numbers = arena.allocateFrom(JAVA_INT, 2, 1);
        qsort.invokeExact(numbers, 2L, JAVA_INT.byteSize(), callback);
        assertThat(numbers.getAtIndex(JAVA_INT, 0)).isEqualTo(1);
      }
    } catch (Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  static int compare(MemorySegment a, MemorySegment b) {
    pause();   // blocking call while a native frame is on the stack
    return Integer.compare(a.get(JAVA_INT, 0), b.get(JAVA_INT, 0));
  }

  private static void pause() {
    try {
      Thread.sleep(BLOCK);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
