package com.talentica.branch_management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("branch")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Branch {
    @Id
    private String id;
    @Indexed(unique = true)
    private String code;
    private String name;
    private String email;
    private String mobileNumber;
    private String pinCode;
    private String state;
    private String city;
    private String district;
}
