package com.howtodoinjava.demo;

import com.howtodoinjava.demo.events.employeeMgmt.AddEmployeeEvent;
import com.howtodoinjava.demo.exception.ApplicationException;
import com.howtodoinjava.demo.model.Employee;
import com.howtodoinjava.demo.service.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
public class AppTest {

  @Autowired
  private ApplicationEvents applicationEvents;

  @Autowired
  private EmployeeService employeeService;

  @Test
  void employeeCreationShouldPublishEvent() throws ApplicationException {

    employeeService.create(new Employee("Alex"));

    Assertions.assertEquals(1, applicationEvents
        .stream(AddEmployeeEvent.class)
        .filter(event -> event.getEmployee().getName().equals("Alex"))
        .count());

    // There are multiple events recorded such as PrepareInstanceEvent,
    // BeforeTestMethodEvent, BeforeTestExecutionEvent, AddEmployeeEvent etc.
    applicationEvents.stream(AddEmployeeEvent.class).forEach(System.out::println);
  }
}
