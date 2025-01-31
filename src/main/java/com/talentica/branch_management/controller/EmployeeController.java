package com.talentica.branch_management.controller;

import com.talentica.branch_management.custom_exception.AppException;
import com.talentica.branch_management.dto.request.EmployeeRequestDTO;
import com.talentica.branch_management.dto.response.EmployeeDTO;
import com.talentica.branch_management.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    private static final String EMPLOYEE_ADD_SUCCESS = "Employee details added successfully!";
    private static final String EMPLOYEE_TRANSFER_SUCCESS = "Employee is transferred to branch successfully!";
    private static final String EMPLOYEE_UPDATE_SUCCESS = "Employee details updated successfully!";
    private static final String EMPLOYEE_DELETE_SUCCESS = "Employee deleted successfully!";
    private static final String SOMETHING_WENT_WRONG_FAILURE = "Something went wrong";

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("/{branchCode}")
    public Page<EmployeeDTO> fetchEmployees(@PathVariable String branchCode,
                                            @RequestParam String filterAnd,
                                            @RequestParam String filterOr,
                                            @PageableDefault Pageable pageable){
        return employeeService.fetchEmployees(branchCode, filterAnd, filterOr, pageable);
    }

    @PostMapping("/add/{branchCode}")
    public ResponseEntity<String> addEmployee(@PathVariable String branchCode, @RequestBody EmployeeRequestDTO employeeRequestDTO){
        try{
            employeeService.addEmployee(employeeRequestDTO, branchCode);
            return new ResponseEntity<>(EMPLOYEE_ADD_SUCCESS, HttpStatus.CREATED);
        }catch (AppException e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(e.getMessage(),e.getCode());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(SOMETHING_WENT_WRONG_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/transfer/{employeeId}")
    public ResponseEntity<String> transferEmployee(@PathVariable String employeeId,
                                                   @RequestParam(required = true) String updatedBranchCode){
        try{
            employeeService.transferEmployee(employeeId,updatedBranchCode);
            return new ResponseEntity<>(EMPLOYEE_TRANSFER_SUCCESS,HttpStatus.OK);
        }catch (AppException e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(e.getMessage(),e.getCode());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(SOMETHING_WENT_WRONG_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {

        }
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<String> updateEmployeeDetails(@PathVariable String employeeId,
                                                        @RequestBody EmployeeRequestDTO employeeRequestDTO){
        try{
            employeeService.updateEmployee(employeeId,employeeRequestDTO);
            return new ResponseEntity<>(EMPLOYEE_UPDATE_SUCCESS,HttpStatus.OK);
        }catch (AppException e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(e.getMessage(),e.getCode());
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return new ResponseEntity<>(SOMETHING_WENT_WRONG_FAILURE,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteEmployeeDetails(@PathVariable String employeeId){
        employeeService.deleteEmployee(employeeId);
        return new ResponseEntity<>(EMPLOYEE_DELETE_SUCCESS,HttpStatus.OK);
    }
}
