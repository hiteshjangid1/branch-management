package com.talentica.branch_management.mapper;

import com.talentica.branch_management.dto.response.BranchEmployeeDTO;
import com.talentica.branch_management.entity.BranchEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BranchEmployeeMapper {

    BranchEmployeeMapper INSTANCE = Mappers.getMapper(BranchEmployeeMapper.class);

    BranchEmployeeDTO map(BranchEmployee branchEmployee);

    List<BranchEmployeeDTO> map(List<BranchEmployee> branchEmployees);
}
