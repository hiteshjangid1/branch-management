package com.talentica.branch_management.repository;

import com.talentica.branch_management.entity.Branch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends MongoRepository<Branch,String> {
    Optional<Branch> findByCode(String code);
}
