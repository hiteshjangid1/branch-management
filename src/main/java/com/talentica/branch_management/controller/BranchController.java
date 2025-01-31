package com.talentica.branch_management.controller;

import com.talentica.branch_management.custom_exception.AppException;
import com.talentica.branch_management.dto.request.BranchRequestDTO;
import com.talentica.branch_management.dto.response.BranchDTO;
import com.talentica.branch_management.dto.response.EmployeeDTO;
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

    private static final String BRANCH_ADD_SUCCESS = "Branch added successfully";
    private static final String BRANCH_UPDATE_SUCCESS = "Branch updated successfully";
    private static final String BRANCH_DELETE_SUCCESS = "Branch deleted successfully";
    private static final String BRANCH_STATUS_UPDATE_SUCCESS = "Branch status updated successfully.";
    private static final String SOMETHING_WENT_WRONG_FAILURE = "Something went wrong";


    @Autowired
    private BranchService branchService;

    @PostMapping
    public Page<BranchDTO> fetchAllBranches(
            @RequestParam String filterOr,
            @RequestParam String filterAnd,
            @PageableDefault Pageable pageable){
        return branchService.fetchBranches(filterOr, filterAnd, pageable);
    }

    @PostMapping
    public ResponseEntity<String> addBranch(@RequestBody BranchRequestDTO branchRequestDTO){
        try {
            branchService.addBranch(branchRequestDTO);
            return new ResponseEntity<>(BRANCH_ADD_SUCCESS, HttpStatus.CREATED);
        }catch (AppException e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(e.getMessage(),e.getCode());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(SOMETHING_WENT_WRONG_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{branchId}")
    public ResponseEntity<String> updateBranch(@RequestBody BranchRequestDTO branchRequestDTO, @PathVariable String branchId){
        try {
            branchService.updateBranch(branchRequestDTO,branchId);
            return new ResponseEntity<>(BRANCH_UPDATE_SUCCESS, HttpStatus.OK);
        }catch (AppException e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(e.getMessage(),e.getCode());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(SOMETHING_WENT_WRONG_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{branchId}")
    public ResponseEntity<String> deleteBranch(@PathVariable String branchId){
        try{
            branchService.deleteBranch(branchId);
            return new ResponseEntity<>(BRANCH_DELETE_SUCCESS, HttpStatus.OK);
        }catch (AppException e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(e.getMessage(),e.getCode());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(SOMETHING_WENT_WRONG_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/employees/{branchCode}")
    public Page<EmployeeDTO> fetchEmployees(@RequestParam String filterOr,
                                            @RequestParam String filterAnd,
                                            @PathVariable String branchCode,
                                            @PageableDefault Pageable pageable){
        try {
            return branchService.fetchEmployees(filterAnd, filterOr, branchCode,pageable);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/status/{branchId}")
    public ResponseEntity<String> updateBranchStatus(@PathVariable String branchId,
                                                     @RequestParam boolean status){
        try{
            branchService.updateBranchStatus(branchId,status);
            return new ResponseEntity<>(BRANCH_STATUS_UPDATE_SUCCESS,HttpStatus.OK);
        }catch (AppException e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(e.getMessage(),e.getCode());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(SOMETHING_WENT_WRONG_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
