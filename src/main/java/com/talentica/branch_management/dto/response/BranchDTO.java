package com.talentica.branch_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchDTO {
    private String id;
    private String code;
    private String name;
    private String email;
    private String mobileNumber;
    private String pinCode;
    private String state;
    private String city;
    private String district;
}
