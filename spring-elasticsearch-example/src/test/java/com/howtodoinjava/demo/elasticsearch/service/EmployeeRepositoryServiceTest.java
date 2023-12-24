package com.howtodoinjava.demo.elasticsearch.service;

import com.howtodoinjava.demo.elasticsearch.entities.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeRepositoryServiceTest {

    @Autowired
    private EmployeeRepositoryService employeeRepositoryService;

    @Test
    void createEmployee() {
        Employee employee = new Employee();
        employee.setName("Bruce");
        employee.setSalary(30000);

        Employee savedEmployee = employeeRepositoryService.createEmployee(employee);

        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getEmployeeId());

        employeeRepositoryService.deleteEmployee(savedEmployee.getEmployeeId());
    }

    @Test
    void updateEmployee() {

        Employee employee = new Employee();
        employee.setName("Clark");
        employee.setSalary(40000);

        Employee savedEmployee = employeeRepositoryService.createEmployee(employee);
        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getEmployeeId());

        savedEmployee.setName("Clark Kent");
        Employee updatedEmployee = employeeRepositoryService.updateEmployee(savedEmployee);
        assertEquals("Clark Kent", updatedEmployee.getName());

        employeeRepositoryService.deleteEmployee(savedEmployee.getEmployeeId());
    }

    @Test
    void getEmployee() throws InterruptedException {
        Employee employee = new Employee();
        employee.setName("Bruce");
        employee.setSalary(20000);

        Employee savedEmployee = employeeRepositoryService.createEmployee(employee);

        Thread.sleep(1000);
        Employee fetchedEmployee = employeeRepositoryService.getEmployee(savedEmployee.getEmployeeId());

        assertNotNull(fetchedEmployee);
        assertEquals(savedEmployee.getEmployeeId(), fetchedEmployee.getEmployeeId());
        assertEquals(20000, fetchedEmployee.getSalary());
        assertEquals("Bruce", fetchedEmployee.getName());

        employeeRepositoryService.deleteEmployee(savedEmployee.getEmployeeId());

    }

    @Test
    void deleteEmployee() {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setSalary(20000);
        Employee savedEmployee = employeeRepositoryService.createEmployee(employee);

        employeeRepositoryService.deleteEmployee(savedEmployee.getEmployeeId());

        Employee fetchedEmployee = employeeRepositoryService.getEmployee(savedEmployee.getEmployeeId());
        assertNull(fetchedEmployee);
    }



    @Test
    void searchEmployeeWithSalaryBetween() throws InterruptedException {
        Employee employee = new Employee();
        employee.setName("Bruce");
        employee.setSalary(20000);
        Employee john =  employeeRepositoryService.createEmployee(employee);


        Employee employee2 = new Employee();
        employee2.setName("Clark");
        employee2.setSalary(30000);
        Employee ronaldo =  employeeRepositoryService.createEmployee(employee2);

        Thread.sleep(1000);
        List<Employee> fetchedEmployees = employeeRepositoryService.searchEmployeeWithSalaryBetween(10000L, 40000L);

        employeeRepositoryService.deleteEmployee(john.getEmployeeId());
        employeeRepositoryService.deleteEmployee(ronaldo.getEmployeeId());

        assertEquals(2, fetchedEmployees.size());
    }

    @Test
    void searchStringQuery() throws InterruptedException {
        Employee employee = new Employee();
        employee.setName("Bruce");
        employee.setSalary(20000);
        Employee john =  employeeRepositoryService.createEmployee(employee);

        Thread.sleep(1000);
        List<Employee> fetchedEmployees = employeeRepositoryService.getEmployeeByName("Bruce");
        employeeRepositoryService.deleteEmployee(john.getEmployeeId());

        assertEquals(1, fetchedEmployees.size());
    }

    @Test
    void getEmployeeByName() throws InterruptedException {

        Employee employee = new Employee();
        employee.setName("Bruce");
        employee.setSalary(20000);
        Employee john =  employeeRepositoryService.createEmployee(employee);

        Employee employee2 = new Employee();
        employee2.setName("Clark");
        employee2.setSalary(20000);
        Employee ronaldo =  employeeRepositoryService.createEmployee(employee2);

        Thread.sleep(1000);
        List<Employee> fetchedEmployees = employeeRepositoryService.searchSalaryQuery(20000L);

        employeeRepositoryService.deleteEmployee(john.getEmployeeId());
        employeeRepositoryService.deleteEmployee(ronaldo.getEmployeeId());

        assertEquals(2, fetchedEmployees.size());
    }
}