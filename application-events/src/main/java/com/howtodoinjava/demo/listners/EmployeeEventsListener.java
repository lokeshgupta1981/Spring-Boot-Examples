package com.howtodoinjava.demo.listners;

import com.howtodoinjava.demo.events.employeeMgmt.AddEmployeeEvent;
import com.howtodoinjava.demo.events.employeeMgmt.DeleteEmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeEventsListener {

  @EventListener
  void handleAddEmployeeEvent(AddEmployeeEvent event) {
    log.info(event.toString());
  }

  @EventListener
  void handleDeleteEmployeeEvent(DeleteEmployeeEvent event) {
    log.info(event.toString());
  }
}
