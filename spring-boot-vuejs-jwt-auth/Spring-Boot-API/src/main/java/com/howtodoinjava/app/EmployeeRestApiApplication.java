package com.howtodoinjava.app;

import com.howtodoinjava.app.entity.Employee;
import com.howtodoinjava.app.repository.EmployeeRepository;
import com.howtodoinjava.app.security.entity.Role;
import com.howtodoinjava.app.security.entity.User;
import com.howtodoinjava.app.security.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeRestApiApplication implements CommandLineRunner {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EmployeeRepository employeeRepository ;

  public static void main(String[] args) {
    SpringApplication.run(EmployeeRestApiApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    employeeRepository.save(new Employee("hamza" , "0435645431" , "hamza.nassour13@gmail.com" , "SDE2"));
    userRepository.save(new User("hamza", "nassour" ,"hamza.nassour13@gmail.com" ,"hamza123" , Role.ADMIN));
  }
}
