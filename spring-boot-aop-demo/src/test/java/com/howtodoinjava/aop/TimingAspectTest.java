package com.howtodoinjava.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TimingAspectTest {
  @Autowired OrderService orders;
  @Autowired TimingRecorder recorder;

  @BeforeEach
  void reset() {
    recorder.clear();
  }

  @Test
  void externalCallRunsAdviceButSelfInvocationDoesNot() {
    assertThat(orders.checkout("A-17")).isEqualTo("receipt:A-17");
    assertThat(recorder.methodNames()).containsExactly("checkout");

    orders.validate("A-17");
    assertThat(recorder.methodNames()).containsExactly("checkout", "validate");
  }

  @Test
  void aroundAdviceRecordsFailedCallAsWell() {
    assertThatThrownBy(() -> orders.validate(" "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("orderId is required");
    assertThat(recorder.methodNames()).isEqualTo(List.of("validate"));
  }
}
