package com.talentica.branch_management.service;

import com.talentica.branch_management.custom_exception.AppException;
import com.talentica.branch_management.dto.request.BranchRequestDTO;
import com.talentica.branch_management.dto.response.BranchDTO;
import com.talentica.branch_management.dto.response.EmployeeDTO;
import com.talentica.branch_management.entity.Branch;
import com.talentica.branch_management.entity.PinCodeMaster;
import com.talentica.branch_management.mapper.BranchMapper;
import com.talentica.branch_management.repository.BranchRepository;
import com.talentica.branch_management.util.QueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService {

    private static final String BRANCH_DELETION_NOT_ALLOWED_ERROR = "Branch deletion can not be done as branch is not present";
    private static final String BRANCH_NOT_PRESENT_ERROR ="This branch is not present";
    private static final String BRANCH_EXISTS_ERROR = "Branch already exists with code: ";
    private static final String BRANCH_NOT_EXIST_ERROR = "Branch does not exist with code: ";
    private static final String PIN_CODE_DETAILS_NOT_PRESENT_ERROR = "Pin code details are not present for pin-code: ";
    private static final String BRANCH_CODE_MISMATCH_ERROR = "Branch code mismatch: existing code is %s, new code is %s";
    private static final String BRANCH_STATUS_MESSAGE = "Branch status is currently : %b";

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PinCodeMasterService pinCodeMasterService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     *
     * @param filterAnd And Filters for search and filter branches
     * @param filterOr Or filter for search and filter branches
     * @param pageable page no and page size for result data
     * @return Paginated response for branches data
     */
    public Page<BranchDTO> fetchBranches(String filterAnd, String filterOr, Pageable pageable) {
        log.info("Fetching all branch details for page details: {}", pageable);
        Query query = QueryBuilder.buildQuery(filterAnd,filterOr);
        Query pagedQuery = query.with(pageable);
        List<Branch> pagedBranches = mongoTemplate.find(pagedQuery, Branch.class);
        long totalBranches = mongoTemplate.count(query,Branch.class);
        List<BranchDTO> branchDTOs = BranchMapper.INSTANCE.map(pagedBranches);
        return new PageImpl<>(branchDTOs, pageable, totalBranches);
    }

    /**
     *
     * @param branchRequestDTO Details for the new branch to be added.
     */
    public void addBranch(BranchRequestDTO branchRequestDTO) throws AppException{
        log.info("Adding a new branch to the system for branch details: {}", branchRequestDTO);

        validateBranchCodeUniqueness(branchRequestDTO.getCode(),false);
        PinCodeMaster pinCodeMaster = fetchValidPinCode(branchRequestDTO.getPinCode());

        Branch branch = BranchMapper.INSTANCE.map(branchRequestDTO);
        BranchMapper.INSTANCE.mapPinCodeMaster(branch, pinCodeMaster);
        branchRepository.save(branch);
    }

    /**
     *
     * @param branchRequestDTO Branch details to be updated
     * @param branchId Primary Key (id) for the existing branch to be updated.
     */
    public void updateBranch(BranchRequestDTO branchRequestDTO, String branchId) throws AppException{
        log.info("Updating branch with ID: {} with details: {}", branchId, branchRequestDTO);

        Branch existingBranch = fetchBranchById(branchId);
        validateBranchCodeConsistency(existingBranch, branchRequestDTO);
        validateBranchStatus(existingBranch,true);
        BranchMapper.INSTANCE.map(existingBranch, branchRequestDTO);
        branchRepository.save(existingBranch);
    }

    /**
     *
     * @param branchId Primary Key (id) for the branch to be deleted.
     */
    public void deleteBranch(String branchId) throws AppException{
        log.info("Deleting branch with ID: {}", branchId);
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,BRANCH_DELETION_NOT_ALLOWED_ERROR));
        validateBranchStatus(branch,false);
        branchRepository.deleteById(branchId);
    }

    /**
     * @param filterAnd And filter (single string parameter)
     * @param filterOr Or filter (single string parameter)
     * @param branchCode Code for the branch for which employees are to be fetched
     * @return Paginated view of employee details.
     */
    public Page<EmployeeDTO> fetchEmployees(String filterAnd, String filterOr, String branchCode, Pageable pageable) throws AppException{
        log.info("Fetching employee details for branch code : {} and page no : {} and page size : {}",branchCode,pageable.getPageNumber(),pageable.getPageSize());
        validateBranchCodeUniqueness(branchCode,true);
        return employeeService.fetchEmployees(filterAnd, filterOr, branchCode,pageable);
    }

    /**
     *
     * @param branchId primay key (id) for the branch whose status is to be updated.
     * @param status active/de-active status
     */

    public void updateBranchStatus(String branchId, boolean status) throws AppException{
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,BRANCH_NOT_PRESENT_ERROR));
        branch.setActive(status);
        branchRepository.save(branch);
    }

    /**
     *
     * @param code branch code , unique for each branch
     * @param present check for present or not present condition
     */
    private void validateBranchCodeUniqueness(String code,boolean present) throws AppException{
        Assert.notNull(code, "Branch code must not be null");
        Optional<Branch> branchOptional = branchRepository.findByCode(code);
        if (!present && branchOptional.isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST,BRANCH_EXISTS_ERROR + code);
        }else if(present && branchOptional.isEmpty()){
            throw new AppException(HttpStatus.BAD_REQUEST,BRANCH_NOT_EXIST_ERROR+ code);
        }
    }

    /**
     *
     * @param pinCode pin code for the area.
     * @return Pin code master which consist area detail like state, city and district
     */
    private PinCodeMaster fetchValidPinCode(String pinCode) throws AppException{
        PinCodeMaster pinCodeMaster = pinCodeMasterService.fetchPinCodeMaster(pinCode);
        if (pinCodeMaster == null) {
            throw new AppException(HttpStatus.BAD_REQUEST,PIN_CODE_DETAILS_NOT_PRESENT_ERROR + pinCode);
        }
        return pinCodeMaster;
    }

    private Branch fetchBranchById(String branchId) throws AppException{
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,BRANCH_NOT_PRESENT_ERROR + branchId));
    }

    private void validateBranchCodeConsistency(Branch existingBranch, BranchRequestDTO branchRequestDTO) throws AppException{
        if (!existingBranch.getCode().equals(branchRequestDTO.getCode())) {
            throw new AppException(HttpStatus.BAD_REQUEST,String.format(
                    BRANCH_CODE_MISMATCH_ERROR,
                    existingBranch.getCode(), branchRequestDTO.getCode()));
        }
    }

    /**
     *
     * @param branch details related to a particular branch
     * @param active branch status will be validated in-accordance to this.
     */
    private void validateBranchStatus(Branch branch,boolean active) throws AppException{
        if(branch.isActive()!=active){
            throw new AppException(HttpStatus.BAD_REQUEST,String.format(BRANCH_STATUS_MESSAGE,active));
        }
    }

}
