package com.talentica.branch_management.dto.request;

import com.talentica.branch_management.constants.PatternConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchRequestDTO {
    @NotBlank(message = "Branch code can not be blank")
    private String code;
    @NotBlank(message = "Branch name can not be blank")
    private String name;
    @Pattern(regexp = PatternConstants.VALID_EMAIL,message = "Email is invalid")
    private String email;
    @Pattern(regexp = PatternConstants.VALID_MOBILE_NUMBER, message = "Mobile number is invalid")
    private String mobileNumber;
    @Pattern(regexp = PatternConstants.VALID_PIN_CODE, message = "Pin code in invalid")
    private String pinCode;
}
