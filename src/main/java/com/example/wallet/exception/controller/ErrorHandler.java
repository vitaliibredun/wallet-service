package com.example.wallet.exception.controller;

import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.InvalidRequestException;
import com.example.wallet.exception.WalletNotFoundException;
import com.example.wallet.exception.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    public ApiError handleException(final Exception exception) {
        log.error("500 {}", exception.getMessage(), exception);
        ApiError error = new ApiError();
        error.setMessage(exception.getMessage());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
        error.setTimeStamp(LocalDateTime.now());
        return error;
    }

    public ApiError handleInsufficientFundsException(final InsufficientFundsException exception) {
        log.error("409 {}", exception.getMessage(), exception);
        ApiError error = new ApiError();
        error.setMessage(exception.getMessage());
        error.setStatus(HttpStatus.CONFLICT.name());
        error.setTimeStamp(LocalDateTime.now());
        return error;
    }

    public ApiError handleInvalidRequestException(final InvalidRequestException exception) {
        log.error("400 {}", exception.getMessage(), exception);
        ApiError error = new ApiError();
        error.setMessage(exception.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setTimeStamp(LocalDateTime.now());
        return error;
    }

    public ApiError handleWalletNotFoundException(final WalletNotFoundException exception) {
        log.error("404 {}", exception.getMessage(), exception);
        ApiError error = new ApiError();
        error.setMessage(exception.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.name());
        error.setTimeStamp(LocalDateTime.now());
        return error;
    }
}
