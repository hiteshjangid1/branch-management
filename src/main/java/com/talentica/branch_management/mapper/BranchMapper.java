package com.talentica.branch_management.mapper;

import com.talentica.branch_management.dto.request.BranchRequestDTO;
import com.talentica.branch_management.dto.response.BranchDTO;
import com.talentica.branch_management.entity.Branch;
import com.talentica.branch_management.entity.PinCodeMaster;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BranchMapper {

    BranchMapper INSTANCE = Mappers.getMapper(BranchMapper.class);

    BranchDTO map(Branch branch);

    List<BranchDTO> map(List<Branch> branches);

    @Mapping(target = "pinCode", source = "pinCode",ignore = true)
    Branch map(BranchRequestDTO branchRequestDTO);

    @Mapping(target = "branch.pinCode", source = "branchRequestDTO.pinCode",ignore = true)
    void map(@MappingTarget Branch branch,BranchRequestDTO branchRequestDTO);

    @Mapping(target = "branch.state", source = "pinCodeMaster.state")
    @Mapping(target = "branch.city", source = "pinCodeMaster.city")
    @Mapping(target = "branch.district", source = "pinCodeMaster.district")
    @Mapping(target = "branch.pinCode", source = "pinCodeMaster.pinCode")
    void mapPinCodeMaster(@MappingTarget Branch branch, PinCodeMaster pinCodeMaster);
}
