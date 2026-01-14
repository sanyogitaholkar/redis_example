package com.example.redis_example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.redis_example.entity.Employee;
import com.example.redis_example.pojo.EmployeeDTO;
import com.example.redis_example.repo.EmployeeRepository;

@Service
@Transactional
public class EmployeeService {

    /*
     * --Handling Caching with POJOs:
     * Use @Cacheable to cache the response when data is retrieved.
     * Use @CachePut to update the cache when data is modified.
     * Use @CacheEvict to remove data from the cache when it’s deleted or updated.
     */

    @Autowired
    private EmployeeRepository repo;

    // 🔥 Read From CACHE
    @Cacheable(value = "employees", key = "#id")
    public Optional<Employee> getEmployeeById(Long id) {
        simulateSlowDB();
        return repo.findById(id); // .orElse(null);
    }
    /*
     * The cache is like the table (employees)
     * The cache key is like the primary key or unique identifier for a row (e.g.,
     * combination of id and phoneNumber)
     * The cached value is like the row itself (an Employee object)
     */

    // 🔥 Write to CACHE- if you comment this @CachePut the Value in database and
    // Cache will differ on updating a employee
    // the changes will only be update in database but not in Cache
    @CachePut(value = "employees", key = "#employee.id")
    public Employee updateEmployee(Employee employee) {
        return repo.save(employee);
    }

    // 🔥Evit CACHE
    @CacheEvict(value = "employees", key = "#id")
    public void deleteEmployee(Long id) {
        repo.deleteById(id);
    }

    public List<Employee> getAllEmployees() {
        return repo.findAll();
    }

    public Employee save(Employee employee) {
        return repo.save(employee);
    }

    // simulate DB delay
    private void simulateSlowDB() {
        try {
            Thread.sleep(2000); // 2 seconds
        } catch (InterruptedException e) {
        }
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Convert entity to POJO (DTO) for caching
    private EmployeeDTO convertToDTO(Employee employee) {
        return new EmployeeDTO(employee.getId(), employee.getFirstname(), employee.getLastname(),
                employee.getDepartment(), employee.getSalary());
    }

    /*
     * @Cacheable(value = "employees", key = "#id")
     * public EmployeeDTO getEmployeeById(Long id) {
     * // First, check if employee is in the cache
     * EmployeeDTO cachedEmployee = (EmployeeDTO)
     * redisTemplate.opsForValue().get("employee::" + id);
     * 
     * if (cachedEmployee != null) {
     * return cachedEmployee;
     * }
     * 
     * // If not in cache, fetch from DB
     * Employee employee = repo.findById(id)
     * .orElseThrow(() -> new RuntimeException("Employee not found"));
     * EmployeeDTO employeeDTO = convertToDTO(employee);
     * 
     * // Cache the employee DTO
     * redisTemplate.opsForValue().set("employee::" + id, employeeDTO);
     * 
     * return employeeDTO;
     * }
     */
}