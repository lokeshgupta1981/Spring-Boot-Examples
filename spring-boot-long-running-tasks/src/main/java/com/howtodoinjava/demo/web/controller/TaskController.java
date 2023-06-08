package com.howtodoinjava.demo.web.controller;

import com.howtodoinjava.demo.web.model.TaskResponse;
import com.howtodoinjava.demo.web.model.TaskRequest;
import com.howtodoinjava.demo.web.model.TaskStatus;
import com.howtodoinjava.demo.web.service.TaskService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  TaskService taskService;

  @PostMapping
  public ResponseEntity<TaskResponse> processAsync(@RequestBody TaskRequest taskRequest, UriComponentsBuilder b) {

    String taskId = UUID.randomUUID().toString();
    UriComponents progressURL = b.path("/tasks/{id}/progress").buildAndExpand(taskId);
    TaskResponse task = new TaskResponse(taskId, taskRequest.getName(), progressURL.toUriString());
    taskService.process(taskId, taskRequest);
    return ResponseEntity.accepted().body(task);
  }

  @Autowired
  KafkaTemplate kafkaTemplate;

  @GetMapping("{taskId}/progress")
  public ResponseEntity<?> processAsync(@PathVariable String taskId) {

    TaskStatus taskStatus = taskService.getLatestTaskStatus(taskId);
    if(taskStatus == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok().body(taskStatus);
  }
}
