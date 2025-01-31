package com.talentica.branch_management.mapper;

import com.talentica.branch_management.dto.request.EmployeeRequestDTO;
import com.talentica.branch_management.dto.response.EmployeeDTO;
import com.talentica.branch_management.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeDTO map(Employee employee);

    List<EmployeeDTO> map(List<Employee> employees);

    Employee map(EmployeeRequestDTO employeeRequestDTO);

    void map(@MappingTarget Employee employee, EmployeeRequestDTO employeeRequestDTO);
}
