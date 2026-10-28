package com.howtodoinjava.mockbean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class ShippingControllerUnitTest {
  @Test
  void usesPlainMockitoWithoutSpring() {
    ShippingRateService rates = mock(ShippingRateService.class);
    when(rates.quoteInCents("A-17")).thenReturn(299);

    ShippingController controller = new ShippingController(rates);
    assertThat(controller.quote("A-17"))
        .isEqualTo(new ShippingController.Quote("A-17", 299));
  }
}
