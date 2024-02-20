package com.bankmas.report.serviceexcel.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bankmas.report.serviceexcel.dto.ErrorResponse;
import com.bankmas.report.serviceexcel.exception.ValidationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationExceptionHandler(ValidationException ex){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.message = ex.getMessage();

        return ResponseEntity.badRequest().body(errorResponse);
    }
}