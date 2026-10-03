package com.howtodoinjava.demo.elasticsearch.service;

import com.howtodoinjava.demo.elasticsearch.entities.Employee;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeOperationsService {

    private final ElasticsearchOperations elasticsearchOperations;

    public EmployeeOperationsService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Employee createEmployee(Employee employee) {
        return elasticsearchOperations.save(employee);
    }

    public Employee updateEmployee(Employee employee) {
        return elasticsearchOperations.save(employee);
    }

    public void deleteEmployee(String employeeId) {
        elasticsearchOperations.delete(employeeId, Employee.class);
    }

    public Employee getEmployee(String employeeId) {
        return elasticsearchOperations.get(employeeId, Employee.class);
    }

    public List<Employee> searchEmployeeWithSalaryBetween(long startingSalary, long endingSalary) {
        Criteria criteria = new Criteria("salary")
                .greaterThan(startingSalary).lessThan(endingSalary);
        Query query = new CriteriaQuery(criteria);

        SearchHits<Employee> searchHits = elasticsearchOperations
                .search(query, Employee.class);

        return searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    public List<Employee> searchStringQuery(String name) {
        Query query = new StringQuery("{ \"match\": { \"name\": { \"query\": \"" + name + "\" } } } ");
        SearchHits<Employee> searchHits = elasticsearchOperations.search(query, Employee.class);
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    public  List<Employee> getAllEmployeesBySalary(long salary) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("salary")
                                .query(salary)
                        )
                )
                .build();

        SearchHits<Employee> searchHits = elasticsearchOperations.search(query, Employee.class);

        return searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();

    }

    public List<Employee> getEmployeeUsingScroll() {
        IndexCoordinates index = IndexCoordinates.of("employees");

        Query searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .matchAll(ma -> ma))
                .withFields("salary")
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHitsIterator<Employee> stream = elasticsearchOperations.searchForStream(searchQuery, Employee.class,
                index);

        List<Employee> employees = new ArrayList<>();
        while (stream.hasNext()) {
            employees.add(stream.next().getContent());
        }

        stream.close();

        return employees;
    }
}
