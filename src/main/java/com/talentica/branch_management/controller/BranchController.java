package com.talentica.branch_management.controller;

import com.talentica.branch_management.dto.request.BranchRequestDTO;
import com.talentica.branch_management.dto.response.BranchDTO;
import com.talentica.branch_management.dto.response.BranchEmployeeDTO;
import com.talentica.branch_management.service.BranchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/branch")
@Slf4j
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public Page<BranchDTO> fetchAllBranches(@PageableDefault Pageable pageable){
        return branchService.fetchBranches(pageable);
    }

    @PostMapping
    public ResponseEntity<String> addBranch(@RequestBody BranchRequestDTO branchRequestDTO){
        try {
            branchService.addBranch(branchRequestDTO);
            return new ResponseEntity<>("Branch added successfully", HttpStatus.CREATED);
        }catch (Exception e){
            log.error("Exception occurred adding a new branch to the system", e);
            return new ResponseEntity<>("Something went wrong while adding branch", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{branchId}")
    public ResponseEntity<String> updateBranch(@RequestBody BranchRequestDTO branchRequestDTO, @PathVariable String branchId){
        try {
            branchService.updateBranch(branchRequestDTO,branchId);
            return new ResponseEntity<>("Branch updated successfully", HttpStatus.CREATED);
        }catch (Exception e){
            log.error("Exception occurred updating branch : {} to the system",branchId, e);
            return new ResponseEntity<>("Something went wrong while updating branch", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{branchId}")
    public ResponseEntity<String> deleteBranch(@PathVariable String branchId){
        branchService.deleteBranch(branchId);
        return new ResponseEntity<>("Branch deleted successfully", HttpStatus.CREATED);
    }

    @GetMapping("/employees/{branchCode}")
    public Page<BranchEmployeeDTO> fetchEmployees(@PageableDefault Pageable pageable, @PathVariable String branchCode){
        try {
            return branchService.fetchEmployees(branchCode,pageable);
        }catch (Exception e){
            log.error("Exception occurred while fetching branch employee for branch code : {}",branchCode, e);
            throw new RuntimeException(e.getMessage());
        }
    }


}
