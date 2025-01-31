package com.talentica.branch_management.custom_exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppException extends Exception{
    private HttpStatus code;
    private String message;
}
