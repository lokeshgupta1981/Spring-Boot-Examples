package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.dao.EmployeeRepository;
import com.howtodoinjava.demo.events.employeeMgmt.AddEmployeeEvent;
import com.howtodoinjava.demo.exception.ApplicationException;
import com.howtodoinjava.demo.model.Employee;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements ApplicationEventPublisherAware {

  EmployeeRepository repository;
  ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public EmployeeService(EmployeeRepository repository) {
    this.repository = repository;
  }

  public Employee create(Employee employee) throws ApplicationException {
    Employee newEmployee = repository.save(employee);
    if (newEmployee != null) {
      applicationEventPublisher.publishEvent(new AddEmployeeEvent(newEmployee));
      return newEmployee;
    }
    throw new ApplicationException("Employee could not be saved");
  }
}
