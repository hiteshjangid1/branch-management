package com.talentica.branch_management.repository;

import com.talentica.branch_management.entity.BranchEmployee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchEmployeeRepository extends MongoRepository<BranchEmployee, String> {
}
