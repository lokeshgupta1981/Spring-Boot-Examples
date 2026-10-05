package com.howtodoinjava.embabel.web;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.AgentProcess;
import com.embabel.agent.core.AgentProcessStatusCode;
import com.embabel.agent.domain.io.UserInput;
import com.howtodoinjava.embabel.domain.ReadingAdvice;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadingPlanController {

  private final AgentPlatform agentPlatform;

  public ReadingPlanController(AgentPlatform agentPlatform) {
    this.agentPlatform = agentPlatform;
  }

  @PostMapping("/reading-plans")
  public ResponseEntity<ReadingAdvice> plan(@RequestBody(required = false) String message)
      throws Exception {
    if (message == null || message.isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    AgentInvocation<ReadingAdvice> invocation =
        AgentInvocation.create(agentPlatform, ReadingAdvice.class);
    AgentProcess process = invocation.runAsync(new UserInput(message.strip()))
        .get(5, TimeUnit.MINUTES);
    if (process.getStatus() != AgentProcessStatusCode.COMPLETED) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
    return ResponseEntity.ok(process.last(ReadingAdvice.class));
  }
}
