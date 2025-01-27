package com.talentica.branch_management.dto.response;

import com.talentica.branch_management.enums.Gender;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class BranchEmployeeDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private Gender gender;
    private Date dob;
}
