package com.talentica.branch_management.service;

import com.talentica.branch_management.custom_exception.AppException;
import com.talentica.branch_management.dto.request.EmployeeRequestDTO;
import com.talentica.branch_management.dto.response.EmployeeDTO;
import com.talentica.branch_management.entity.Employee;
import com.talentica.branch_management.mapper.EmployeeMapper;
import com.talentica.branch_management.repository.EmployeeRepository;
import com.talentica.branch_management.util.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EmployeeService {

    private static final String DUPLICATE_EMPLOYEE_ADD_ERROR = "Employee details are duplicate and hence can not be added";
    private static final String EMPLOYEE_INVALID_ID_ERROR = "Invalid employee Id";
    private static final String DUPLICATE_EMPLOYEE_ERROR = "Employee with same personal details are already present";
    private static final String EMPLOYEE_NOT_PRESENT_ERROR = "Employee is not present";
    private static final String EMPLOYEE_PART_OF_BRANCH_ERROR = "Employee is already a part of branch";

    @Autowired
    private EmployeeRepository repository;

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
    public Page<EmployeeDTO> fetchEmployees(String filterAnd, String filterOr, String branchCode, Pageable pageable){
        log.info("Fetching details for employees with branch code : {}",branchCode);
        Query query = QueryBuilder.buildQuery(filterAnd, filterOr);
        Query paginatedQuery = query.with(pageable);
        List<Employee> employees = mongoTemplate.find(paginatedQuery, Employee.class);
        long total = mongoTemplate.count(query, Employee.class);
        List<EmployeeDTO> branchEmployeeDTOS = EmployeeMapper.INSTANCE.map(employees);
        return new PageImpl<>(branchEmployeeDTOS,pageable,total);
    }

    /**
     *
     * @param employeeRequestDTO Details for the employee to be added.
     * @param branchCode Code for the branch for which employee to be added.
     * @throws AppException If Employee details are matched with other employee.
     */
    public void addEmployee(EmployeeRequestDTO employeeRequestDTO, String branchCode) throws AppException {
        log.info("Adding employee with details : {} to branch : {}",employeeRequestDTO, branchCode);
        Employee employee = EmployeeMapper.INSTANCE.map(employeeRequestDTO);
        String email = employee.getEmail();
        String mobileNumber = employee.getMobileNumber();
        if(isExistingEmployee(email, mobileNumber)){
            throw new AppException(HttpStatus.BAD_REQUEST, DUPLICATE_EMPLOYEE_ADD_ERROR);
        }
        repository.save(employee);
    }

    /**
     *
     * @param employeeId unique id for the employee
     * @param branchCode Latest branch code which need to be updated
     */
    public void transferEmployee(String employeeId, String branchCode) throws AppException{
        log.info("Employee with id : {} will be transferred to branch with code : {}", employeeId, branchCode);
        Optional<Employee> employeeOptional = repository.findById(employeeId);
        Employee employee = validateEmployeePresenceAndBranch(branchCode, employeeOptional);
        employee.setBranchCode(branchCode);
        repository.save(employee);
    }

    /**
     *
     * @param employeeId Id for the employee whose details are to be updated
     * @param employeeRequestDTO employee details object which are to be updated
     * @throws AppException when any other user have same details
     */
    public void updateEmployee(String employeeId, EmployeeRequestDTO employeeRequestDTO) throws AppException{
        log.info("Employee details are being updated for employeeId : {} and details : {}",employeeId, employeeRequestDTO);
        String email = employeeRequestDTO.getEmail();
        String mobileNumber = employeeRequestDTO.getMobileNumber();
        List<Employee> employees = repository.findEmployeeByEmailOrMobileNumber(email,mobileNumber);
        Optional<Employee> employeeOptional = repository.findById(employeeId);
        Employee employee = validateEmployeePersonalDetails(employeeId, employeeOptional, employees);
        EmployeeMapper.INSTANCE.map(employee, employeeRequestDTO);
        repository.save(employee);
    }

    /**
     *
     * @param employeeId Id for which employee will be deleted
     */
    public void deleteEmployee(String employeeId){
        log.info("Employee deletion for employee Id : {} has started.",employeeId);
        repository.deleteById(employeeId);
    }

    /**
     *
     * @param employeeId Id for the employee whose details are to be updated
     * @param employeeOptional employeeOptional based on employee Id
     * @param employees List of employees with same personal details
     * @return Employee entity Object
     * @throws AppException when any other user with the same details are already present.
     */
    private Employee validateEmployeePersonalDetails(String employeeId, Optional<Employee> employeeOptional, List<Employee> employees) throws AppException {
        if(employeeOptional.isEmpty()){
            throw new AppException(HttpStatus.BAD_REQUEST,EMPLOYEE_INVALID_ID_ERROR);
        }else if(employees.size()>1 || (employees.size()==1 && !employees.getFirst().getId().equals(employeeId))){
            throw new AppException(HttpStatus.BAD_REQUEST,DUPLICATE_EMPLOYEE_ERROR);
        }
        return employeeOptional.get();
    }

    /**
     *
     * @param branchCode Branch code for the specified branch
     * @param employeeOptional Optional for the employee (can be empty)
     * @return return Value of Employee entity
     * @throws AppException when employee is not present or employee is already part of branch
     */
    private Employee validateEmployeePresenceAndBranch(String branchCode, Optional<Employee> employeeOptional) throws AppException {
        if(employeeOptional.isEmpty()){
            throw new AppException(HttpStatus.BAD_REQUEST,EMPLOYEE_NOT_PRESENT_ERROR);
        }else if(employeeOptional.get().getBranchCode().equals(branchCode)){
            throw new AppException(HttpStatus.BAD_REQUEST,EMPLOYEE_PART_OF_BRANCH_ERROR);
        }

        return employeeOptional.get();
    }

    /**
     *
     * @param email Email of the possible employee
     * @param mobileNumber Mobile number for the possible employee
     * @return Boolean which signifies if employee details are present in the system
     */
    private boolean isExistingEmployee(String email, String mobileNumber){
        log.info("Validating employee with personal details email: {} and mobileNumber : {}",email,mobileNumber);
        return !repository.findEmployeeByEmailOrMobileNumber(email,mobileNumber).isEmpty();
    }

}
