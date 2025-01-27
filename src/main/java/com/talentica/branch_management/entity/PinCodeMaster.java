package com.talentica.branch_management.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("pin_code_master")
public class PinCodeMaster {
    @Id
    private String id;
    private String pinCode;
    private String state;
    private String city;
    private String district;
}
