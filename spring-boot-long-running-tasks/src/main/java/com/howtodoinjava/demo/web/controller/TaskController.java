package com.howtodoinjava.demo.web.controller;

import com.howtodoinjava.demo.web.model.TaskResponse;
import com.howtodoinjava.demo.web.model.TaskRequest;
import com.howtodoinjava.demo.web.service.TaskService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  TaskService taskService;

  @PostMapping
  public ResponseEntity<TaskResponse> processAsync(@RequestBody TaskRequest taskRequest) {

    String taskId = UUID.randomUUID().toString();
    TaskResponse task = new TaskResponse(taskId, taskRequest.getName());

    taskService.process(taskId, taskRequest);

    return ResponseEntity.accepted().body(task);
  }
}
