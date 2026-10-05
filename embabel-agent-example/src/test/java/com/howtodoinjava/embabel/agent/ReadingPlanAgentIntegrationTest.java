package com.howtodoinjava.embabel.agent;

import static org.assertj.core.api.Assertions.assertThat;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.test.integration.EmbabelMockitoIntegrationTest;
import com.howtodoinjava.embabel.domain.NoBooksFound;
import com.howtodoinjava.embabel.domain.ReadingAdvice;
import com.howtodoinjava.embabel.domain.ReadingPlan;
import com.howtodoinjava.embabel.domain.ReadingRequest;
import org.junit.jupiter.api.Test;

class ReadingPlanAgentIntegrationTest extends EmbabelMockitoIntegrationTest {

  @Test
  void plansAFantasyBook() {
    whenCreateObject(prompt -> prompt.contains("Extract the reader"), ReadingRequest.class)
        .thenReturn(new ReadingRequest("Lokesh", "fantasy", 10));
    whenCreateObject(prompt -> prompt.contains("Pick one book"), ReadingPlan.class)
        .thenReturn(new ReadingPlan("Lokesh", "The Hobbit", 31, "Enjoy the trip to Middle-earth!"));

    ReadingAdvice advice = AgentInvocation.create(agentPlatform, ReadingAdvice.class)
        .invoke(new UserInput("I am Lokesh. A fantasy book in 10 days, please."));

    assertThat(advice).isEqualTo(
        new ReadingPlan("Lokesh", "The Hobbit", 31, "Enjoy the trip to Middle-earth!"));
    verifyCreateObjectMatching(prompt -> prompt.contains("The Hobbit (310 pages)"),
        ReadingPlan.class, llm -> llm.getTools().size() == 1);
  }

  @Test
  void takesTheOtherGoalWhenTheShelfIsEmpty() {
    whenCreateObject(prompt -> prompt.contains("Extract the reader"), ReadingRequest.class)
        .thenReturn(new ReadingRequest("Alex", "poetry", 7));

    ReadingAdvice advice = AgentInvocation.create(agentPlatform, ReadingAdvice.class)
        .invoke(new UserInput("Alex here, any poetry for next week?"));

    assertThat(advice).isEqualTo(new NoBooksFound("Alex", "poetry"));
    verifyCreateObject(prompt -> prompt.contains("Extract the reader"), ReadingRequest.class);
    verifyNoMoreInteractions();
  }
}
