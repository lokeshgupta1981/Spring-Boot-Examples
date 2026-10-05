package com.howtodoinjava.embabel.agent;

import static org.assertj.core.api.Assertions.assertThat;

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.test.unit.FakeOperationContext;
import com.embabel.agent.test.unit.LlmInvocation;
import com.howtodoinjava.embabel.domain.NoBooksFound;
import com.howtodoinjava.embabel.domain.ReadingPlan;
import com.howtodoinjava.embabel.domain.ReadingRequest;
import com.howtodoinjava.embabel.domain.Shelf;
import com.howtodoinjava.embabel.library.LibraryCatalog;
import org.junit.jupiter.api.Test;

class ReadingPlanAgentTest {

  private final ReadingPlanAgent agent = new ReadingPlanAgent(new LibraryCatalog());

  @Test
  void extractRequestSendsTheMessageToTheLlm() {
    var context = FakeOperationContext.create();
    context.expectResponse(new ReadingRequest("Lokesh", "fantasy", 10));

    ReadingRequest request = agent.extractRequest(
        new UserInput("I am Lokesh, a fantasy book in 10 days please"), context);

    assertThat(request).isEqualTo(new ReadingRequest("Lokesh", "fantasy", 10));
    LlmInvocation call = context.getLlmInvocations().getFirst();
    assertThat(call.getPrompt()).contains("I am Lokesh, a fantasy book in 10 days please");
    assertThat(call.getInteraction().getLlm().getTemperature()).isEqualTo(0.0);
  }

  @Test
  void findBooksUsesTheCatalogWithoutTheLlm() {
    Shelf fantasy = agent.findBooks(new ReadingRequest("Lokesh", "Fantasy ", 10));
    Shelf poetry = agent.findBooks(new ReadingRequest("Lokesh", "poetry", 10));

    assertThat(fantasy.books()).hasSize(2);
    assertThat(poetry.isEmpty()).isTrue();
    assertThat(agent.hasBooks(fantasy)).isTrue();
    assertThat(agent.emptyShelf(poetry)).isTrue();
  }

  @Test
  void planReadingGivesTheLlmTheShelfAndTheTool() {
    var context = FakeOperationContext.create();
    var expected = new ReadingPlan("Lokesh", "The Hobbit", 31, "You can do it!");
    context.expectResponse(expected);
    var request = new ReadingRequest("Lokesh", "fantasy", 10);
    Shelf shelf = new LibraryCatalog().shelfFor("fantasy");

    ReadingPlan plan = agent.planReading(request, shelf, context);

    assertThat(plan).isEqualTo(expected);
    LlmInvocation call = context.getLlmInvocations().getFirst();
    assertThat(call.getPrompt()).contains("The Hobbit (310 pages)", "Mistborn (541 pages)");
    assertThat(call.getInteraction().getTools())
        .extracting(tool -> tool.getDefinition().getName())
        .containsExactly("calculateDailyPages");
  }

  @Test
  void noBooksFoundNeedsNoLlm() {
    NoBooksFound result = agent.noBooksFound(
        new ReadingRequest("Lokesh", "poetry", 10), new Shelf("poetry", null));

    assertThat(result).isEqualTo(new NoBooksFound("Lokesh", "poetry"));
  }
}
