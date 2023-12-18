package com.howtodoinjava.demo.listners;

import com.howtodoinjava.demo.events.employeeMgmt.DeleteEmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeleteEmployeeEventListener implements ApplicationListener<DeleteEmployeeEvent> {

  @Override
  public void onApplicationEvent(DeleteEmployeeEvent event) {
    log.info(event.toString());
  }
}
