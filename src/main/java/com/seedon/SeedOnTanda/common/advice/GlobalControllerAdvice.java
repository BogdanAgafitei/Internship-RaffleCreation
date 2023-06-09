package com.seedon.SeedOnTanda.common.advice;

import com.seedon.SeedOnTanda.common.advice.response.ApiError;
import com.seedon.SeedOnTanda.common.advice.response.Errors;
import com.seedon.SeedOnTanda.storage.message.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidateErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        final var errors = new Errors(Objects.requireNonNull(ex.getFieldError()).getField(), errorMessage);
        final var response = createErrorMessage(String.format("%s %s", LocalDate.now(), LocalTime.now()),
                HttpStatus.valueOf(ex.getStatusCode().value()),
                "Validation failed",
                request.getRequestURI(),
                List.of(errors));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("File too large!"));
    }


    private <T> ApiError<T> createErrorMessage(String timestamp, HttpStatus error, String message, String path, T data) {
        return new ApiError<>(
                timestamp, error.value(), error, message, path, data);
    }

}
