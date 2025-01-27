package com.talentica.branch_management.service;

import com.talentica.branch_management.dto.request.BranchRequestDTO;
import com.talentica.branch_management.dto.response.BranchDTO;
import com.talentica.branch_management.dto.response.BranchEmployeeDTO;
import com.talentica.branch_management.entity.Branch;
import com.talentica.branch_management.entity.PinCodeMaster;
import com.talentica.branch_management.mapper.BranchMapper;
import com.talentica.branch_management.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PinCodeMasterService pinCodeMasterService;

    @Autowired
    private BranchEmployeeService branchEmployeeService;

    /**
     *
     * @param pageable page size and page number for branch list
     * @return Paged view of the given branches
     */
    public Page<BranchDTO> fetchBranches(Pageable pageable) {
        log.info("Fetching all branch details for page details: {}", pageable);
        Page<Branch> pagedBranches = branchRepository.findAll(pageable);
        List<BranchDTO> branchDTOs = BranchMapper.INSTANCE.map(pagedBranches.toList());
        return new PageImpl<>(branchDTOs, pageable, pagedBranches.getTotalElements());
    }

    /**
     *
     * @param branchRequestDTO Details for the new branch to be added.
     */
    public void addBranch(BranchRequestDTO branchRequestDTO) {
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
    public void updateBranch(BranchRequestDTO branchRequestDTO, String branchId) {
        log.info("Updating branch with ID: {} with details: {}", branchId, branchRequestDTO);

        Branch existingBranch = fetchBranchById(branchId);
        validateBranchCodeConsistency(existingBranch, branchRequestDTO);

        BranchMapper.INSTANCE.map(existingBranch, branchRequestDTO);
        branchRepository.save(existingBranch);
    }

    /**
     *
     * @param branchId Primary Key (id) for the branch to be deleted.
     */
    public void deleteBranch(String branchId) {
        log.info("Deleting branch with ID: {}", branchId);
        branchRepository.deleteById(branchId);
    }

    /**
     *
     * @param branchCode Code for the branch for which employees are to be fetched
     * @return Paginated view of employee details.
     */
    public Page<BranchEmployeeDTO> fetchEmployees(String branchCode,Pageable pageable){
        log.info("Fetching employee details for branch code : {} and page no : {} and page size : {}",branchCode,pageable.getPageNumber(),pageable.getPageSize());
        validateBranchCodeUniqueness(branchCode,true);
        return branchEmployeeService.fetchEmployees(branchCode,pageable);
    }


    private void validateBranchCodeUniqueness(String code,boolean present) {
        Assert.notNull(code, "Branch code must not be null");
        Optional<Branch> branchOptional = branchRepository.findByCode(code);
        if (!present && branchOptional.isPresent()) {
            throw new IllegalArgumentException("Branch already exists with code: " + code);
        }else if(present && branchOptional.isEmpty()){
            throw new IllegalArgumentException("Branch does not exist with code: " + code);
        }
    }

    private PinCodeMaster fetchValidPinCode(String pinCode) {
        Assert.notNull(pinCode, "Pin code must not be null");
        PinCodeMaster pinCodeMaster = pinCodeMasterService.fetchPinCodeMaster(pinCode);
        if (pinCodeMaster == null) {
            throw new IllegalArgumentException("Pin code details are not present for pin-code: " + pinCode);
        }
        return pinCodeMaster;
    }

    private Branch fetchBranchById(String branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("No branch is present with ID: " + branchId));
    }

    private void validateBranchCodeConsistency(Branch existingBranch, BranchRequestDTO branchRequestDTO) {
        Assert.notNull(branchRequestDTO.getCode(), "Branch code in the request must not be null");
        if (!existingBranch.getCode().equals(branchRequestDTO.getCode())) {
            throw new IllegalArgumentException(String.format(
                    "Branch code mismatch: existing code is %s, new code is %s",
                    existingBranch.getCode(), branchRequestDTO.getCode()));
        }
    }

}
