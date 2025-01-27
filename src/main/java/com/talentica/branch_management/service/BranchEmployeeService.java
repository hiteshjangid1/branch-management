package com.talentica.branch_management.service;

import com.talentica.branch_management.dto.response.BranchEmployeeDTO;
import com.talentica.branch_management.entity.BranchEmployee;
import com.talentica.branch_management.mapper.BranchEmployeeMapper;
import com.talentica.branch_management.repository.BranchEmployeeRepository;
import com.talentica.branch_management.util.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BranchEmployeeService {

    @Autowired
    private BranchEmployeeRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     *
     * @param filterAnd And Filter passed as a single argument
     * @param filterOr Or filters passed as a single argument
     * @param branchCode code for the branch for which employees are to be fetched.
     * @param pageable page no and page size for the data.
     * @return Paginated response for employees of a single branch
     */
    public Page<BranchEmployeeDTO> fetchEmployees(String filterAnd, String filterOr, String branchCode, Pageable pageable){
        log.info("Fetching details for employees with branch code : {}",branchCode);
        Query query = QueryBuilder.buildQuery(filterAnd, filterOr);
        Query paginatedQuery = query.with(pageable);
        List<BranchEmployee> branchEmployees = mongoTemplate.find(paginatedQuery, BranchEmployee.class);
        long total = mongoTemplate.count(query, BranchEmployee.class);
        List<BranchEmployeeDTO> branchEmployeeDTOS = BranchEmployeeMapper.INSTANCE.map(branchEmployees);
        return new PageImpl<>(branchEmployeeDTOS,pageable,total);
    }
}
