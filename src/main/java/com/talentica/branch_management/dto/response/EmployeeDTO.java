package com.talentica.branch_management.dto.response;

import com.talentica.branch_management.enums.Gender;
import lombok.Data;

import java.util.Date;

@Data
public class EmployeeDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private Gender gender;
    private Date dob;
}
