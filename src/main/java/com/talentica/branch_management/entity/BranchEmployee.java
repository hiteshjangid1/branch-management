package com.talentica.branch_management.entity;

import com.talentica.branch_management.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("branch_employee")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchEmployee {
    @Id
    private String id;
    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private Gender gender;
    private Date dob;
}
