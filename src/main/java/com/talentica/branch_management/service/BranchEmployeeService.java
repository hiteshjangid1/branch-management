package com.talentica.branch_management.service;

import com.talentica.branch_management.dto.response.BranchEmployeeDTO;
import com.talentica.branch_management.entity.BranchEmployee;
import com.talentica.branch_management.mapper.BranchEmployeeMapper;
import com.talentica.branch_management.repository.BranchEmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BranchEmployeeService {

    @Autowired
    private BranchEmployeeRepository repository;

    public Page<BranchEmployeeDTO> fetchEmployees(String branchCode, Pageable pageable){
        log.info("Fetching details for employees with branch code : {}",branchCode);
        Page<BranchEmployee> branchEmployeesPage = repository.findAll(pageable);
        long total = branchEmployeesPage.getTotalElements();
        List<BranchEmployeeDTO> branchEmployeeDTOS = BranchEmployeeMapper.INSTANCE.map(branchEmployeesPage.toList());
        return new PageImpl<>(branchEmployeeDTOS,pageable,total);
    }
}
