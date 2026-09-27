package com.mike.bank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(
            AccountNotFoundException exception) {

        ErrorResponse error = new ErrorResponse(
                "ACCOUNT_NOT_FOUND",
                "Account " + exception.getAccountNumber() +
                        " was not found"
        );
        return ResponseEntity
                .status(404)
                .body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(
            InsufficientFundsException exception) {

        ErrorResponse error = new ErrorResponse(
                "INSUFFICIENT_FUNDS",
                exception.getMessage()
        );
        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception) {

        ErrorResponse error = new ErrorResponse(
                "INVALID_ARGUMENT",
                exception.getMessage()
        );
        return ResponseEntity
                .badRequest()
                .body(error);
    }

}
