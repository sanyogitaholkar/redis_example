package com.example.redis_example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.redis_example.entity.Employee;
import com.example.redis_example.service.EmployeeService;
import com.example.redis_example.service.RateLimiterService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    /*
     * Redis Usage :
     * Caching frequently accessed data to improve access time.
     * Session storage for web applications
     * Real-time analytics and leader boards.
     * Managing queues or task lists in background job systems.
     * Geospatial
     */

    @Autowired
    private EmployeeService service;

    /*
     * DO not send the id from post man or else - error
     * org.hibernate.StaleObjectStateException: Row was already updated or deleted
     * by another transaction for entity [com.example.redis_example.pojo.Employee
     * with id '1']
     */
    @PostMapping
    public Employee save(@RequestBody Employee employee) {
        return service.save(employee);
    }

    @GetMapping("/{id}")
    public Optional<Employee> getById(@PathVariable Long id) {
        return service.getEmployeeById(id);
    }

    @GetMapping
    public List<Employee> getAll() {
        return service.getAllEmployees();
    }

    @PutMapping
    public Employee update(@RequestBody Employee employee) {
        return service.updateEmployee(employee);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteEmployee(id);
        return "Deleted";
    }

    @Autowired
    private RateLimiterService rateLimiterService;

    @GetMapping("/rate_check/{id}")
    public ResponseEntity<Employee> getEmployee(
            @PathVariable Long id,
            HttpServletRequest request) {

        String clientId = request.getRemoteAddr(); // IP-based

        if (!rateLimiterService.isAllowed(clientId)) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .build();
        }

        // dummy response for practice
        Employee emp = new Employee();
        emp.setId(id);
        emp.setFirstname("John");
        emp.setLastname("Doe");

        return ResponseEntity.ok(emp);
    }

}
