package com.howtodoinjava.demo.elasticsearch.service;

import com.howtodoinjava.demo.elasticsearch.entities.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeOperationsServiceTest {

    @Autowired
    private EmployeeOperationsService employeeOperationsService;

    @Test
    void createEmployee() {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);

        Employee savedEmployee = employeeOperationsService.createEmployee(employee);

        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getEmployeeId());

        employeeOperationsService.deleteEmployee(savedEmployee.getEmployeeId());
    }

    @Test
    void updateEmployee() {

        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);

        Employee savedEmployee = employeeOperationsService.createEmployee(employee);
        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getEmployeeId());

        savedEmployee.setName("John Doe");
        Employee updatedEmployee = employeeOperationsService.updateEmployee(savedEmployee);
        assertEquals("John Doe", updatedEmployee.getName());

        employeeOperationsService.deleteEmployee(savedEmployee.getEmployeeId());
    }

    @Test
    void getEmployee() throws InterruptedException {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);

        Employee savedEmployee = employeeOperationsService.createEmployee(employee);

        Thread.sleep(1000);
        Employee fetchedEmployee = employeeOperationsService.getEmployee(savedEmployee.getEmployeeId());

        assertNotNull(fetchedEmployee);
        assertEquals(savedEmployee.getEmployeeId(), fetchedEmployee.getEmployeeId());
        assertEquals(20000, fetchedEmployee.getSalary());
        assertEquals("John", fetchedEmployee.getName());

        employeeOperationsService.deleteEmployee(savedEmployee.getEmployeeId());
    }

    @Test
    void deleteEmployee() {

        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);
        Employee savedEmployee = employeeOperationsService.createEmployee(employee);

        employeeOperationsService.deleteEmployee(savedEmployee.getEmployeeId());

        Employee fetchedEmployee = employeeOperationsService.getEmployee(savedEmployee.getEmployeeId());
        assertNull(fetchedEmployee);
    }

    @Test
    void searchEmployeeWithSalaryBetween() throws InterruptedException {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);
        Employee john =  employeeOperationsService.createEmployee(employee);


        Employee employee2 = new Employee();
        employee2.setName("Ronaldo");
        employee2.setSalary(30000);
        Employee ronaldo =  employeeOperationsService.createEmployee(employee2);

        Thread.sleep(1000);
        List<Employee> fetchedEmployees = employeeOperationsService.searchEmployeeWithSalaryBetween(10000L, 40000L);

        employeeOperationsService.deleteEmployee(john.getEmployeeId());
        employeeOperationsService.deleteEmployee(ronaldo.getEmployeeId());

       assertEquals(2, fetchedEmployees.size());
    }

    @Test
    void searchStringQuery() throws InterruptedException {

        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);
        Employee john =  employeeOperationsService.createEmployee(employee);

        Thread.sleep(1000);
        List<Employee> fetchedEmployees = employeeOperationsService.searchStringQuery("John");
        employeeOperationsService.deleteEmployee(john.getEmployeeId());

        assertEquals(1, fetchedEmployees.size());
    }

    @Test
    void getAggregate() throws InterruptedException {

        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);
        Employee john =  employeeOperationsService.createEmployee(employee);

        Employee employee2 = new Employee();
        employee2.setName("Ronaldo");
        employee2.setSalary(20000);
        Employee ronaldo =  employeeOperationsService.createEmployee(employee2);

        Thread.sleep(1000);
        List<Employee> fetchedEmployees = employeeOperationsService.getAllEmployeesBySalary(20000L);

        employeeOperationsService.deleteEmployee(john.getEmployeeId());
        employeeOperationsService.deleteEmployee(ronaldo.getEmployeeId());

        assertEquals(2, fetchedEmployees.size());
    }

    @Test
    void getEmployeeUsingScroll() throws InterruptedException {

        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);
        Employee john =  employeeOperationsService.createEmployee(employee);

        Employee employee2 = new Employee();
        employee2.setName("Ronaldo");
        employee2.setSalary(20000);
        Employee ronaldo =  employeeOperationsService.createEmployee(employee2);

        Thread.sleep(1000);
        List<Employee> fetchedEmployees = employeeOperationsService.getEmployeeUsingScroll();

        employeeOperationsService.deleteEmployee(john.getEmployeeId());
        employeeOperationsService.deleteEmployee(ronaldo.getEmployeeId());

        assertEquals(2, fetchedEmployees.size());
    }
}