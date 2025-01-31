package com.talentica.branch_management.dto.request;

import com.talentica.branch_management.constants.PatternConstants;
import com.talentica.branch_management.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

@Data
public class EmployeeRequestDTO {
    @NotBlank(message = "Employee first name must not be blank")
    private String firstName;
    private String lastName;
    @Pattern(regexp = PatternConstants.VALID_EMAIL,message = "Email is invalid")
    private String email;
    @Pattern(regexp = PatternConstants.VALID_MOBILE_NUMBER, message = "Mobile number is invalid")
    private String mobileNumber;
    @NotNull(message = "Gender is mandatory")
    private Gender gender;
    @NotNull(message = "Date of the birth is mandatory")
    private Date dob;
}
