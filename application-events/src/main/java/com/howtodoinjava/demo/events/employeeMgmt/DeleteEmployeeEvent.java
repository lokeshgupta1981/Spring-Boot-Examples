package com.howtodoinjava.demo.events.employeeMgmt;

import com.howtodoinjava.demo.model.Employee;
import org.springframework.context.ApplicationEvent;

public class DeleteEmployeeEvent extends ApplicationEvent {

  public DeleteEmployeeEvent(Employee employee) {
    super(employee);
  }

  @Override
  public String toString() {
    return "ApplicationEvent: Employee Deleted :: " + this.getSource();
  }
}
