package com.example.redis_example.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.redis_example.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional findById(Long id);

    Employee save(Employee employee);

    void deleteById(Long id);

    List findAll();

}
