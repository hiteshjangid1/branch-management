package com.talentica.branch_management.repository;

import com.talentica.branch_management.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    @Query("{$or : [{ email : ?0}, {mobileNumber : ?1}]}")
    List<Employee> findEmployeeByEmailOrMobileNumber(String email, String mobileNumber);
}
