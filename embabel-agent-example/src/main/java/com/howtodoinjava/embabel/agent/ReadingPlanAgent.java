package com.howtodoinjava.embabel.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Condition;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import com.howtodoinjava.embabel.domain.NoBooksFound;
import com.howtodoinjava.embabel.domain.ReadingPlan;
import com.howtodoinjava.embabel.domain.ReadingRequest;
import com.howtodoinjava.embabel.domain.Shelf;
import com.howtodoinjava.embabel.library.LibraryCatalog;
import java.util.stream.Collectors;

@Agent(description = "Creates a reading plan from a reader's message and the books in the library")
public class ReadingPlanAgent {

  private final LibraryCatalog catalog;

  public ReadingPlanAgent(LibraryCatalog catalog) {
    this.catalog = catalog;
  }

  // 1. LLM step: free text in, typed object out
  @Action(description = "Extract the reader, the genre and the number of days")
  public ReadingRequest extractRequest(UserInput userInput, OperationContext context) {
    return context.ai()
        .withLlm(LlmOptions.withDefaultLlm().withTemperature(0.0))
        .createObject("""
            Extract the reader's first name, the book genre and the number of days
            from this message. Write the genre in lowercase.
            Message: %s
            """.formatted(userInput.getContent()), ReadingRequest.class);
  }

  // 2. Plain Java step: no LLM call
  @Action(description = "Find the library books of the requested genre",
      post = {"hasBooks", "emptyShelf"})
  public Shelf findBooks(ReadingRequest request) {
    return catalog.shelfFor(request.genre());
  }

  @Condition(name = "hasBooks")
  public boolean hasBooks(Shelf shelf) {
    return !shelf.isEmpty();
  }

  @Condition(name = "emptyShelf")
  public boolean emptyShelf(Shelf shelf) {
    return shelf.isEmpty();
  }

  // 3. LLM step with a tool, reached only when the shelf has books
  @AchievesGoal(description = "A reading plan has been created for the reader")
  @Action(pre = "hasBooks", description = "Pick one book and plan the pages per day")
  public ReadingPlan planReading(ReadingRequest request, Shelf shelf, OperationContext context) {
    String books = shelf.books().stream()
        .map(book -> "- " + book.title() + " (" + book.pages() + " pages)")
        .collect(Collectors.joining("\n"));
    return context.ai()
        .withLlm(LlmOptions.withDefaultLlm().withTemperature(0.2))
        .withToolObject(new ReadingTools())
        .createObject("""
            %s wants to finish one %s book in %d days.
            Pick one book from this list:
            %s
            Pick the book that is easiest to finish in time.
            Call the calculateDailyPages tool with the book's pages and the days.
            Put a one-sentence note that encourages the reader in the note field.
            """.formatted(request.reader(), request.genre(), request.days(), books),
            ReadingPlan.class);
  }

  // 3b. Plain Java step, reached only when the shelf is empty
  @AchievesGoal(description = "The reader was told that the genre has no books")
  @Action(pre = "emptyShelf", description = "Report that the genre has no books")
  public NoBooksFound noBooksFound(ReadingRequest request, Shelf shelf) {
    return new NoBooksFound(request.reader(), shelf.genre());
  }
}
