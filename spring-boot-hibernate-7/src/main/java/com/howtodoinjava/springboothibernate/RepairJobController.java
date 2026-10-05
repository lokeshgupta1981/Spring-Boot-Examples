package com.howtodoinjava.springboothibernate;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class RepairJobController {

  private final RepairShopService service;

  public RepairJobController(RepairShopService service) {
    this.service = service;
  }

  @GetMapping
  public List<RepairJobView> byStatus(@RequestParam RepairStatus status) {
    return service.findByStatus(status).stream().map(RepairJobView::of).toList();
  }

  @GetMapping("/{id}")
  public RepairJobView one(@PathVariable Long id) {
    return RepairJobView.of(service.find(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RepairJobView create(@RequestBody NewRepairJob request) {
    RepairJob job = service.receive(request.mechanicId(), request.bikeModel(), request.problem(), request.cost());
    return RepairJobView.of(job);
  }

  @PatchMapping("/{id}/complete")
  public RepairJobView complete(@PathVariable Long id) {
    service.complete(id);
    return RepairJobView.of(service.find(id));
  }
}
