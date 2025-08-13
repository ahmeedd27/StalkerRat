package com.ahmed.AhmedMohmoud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.ahmed.AhmedMohmoud.exception.BusinessErrorCodes.*;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                  ExceptionResponse.builder()
                          .businessErrorCode(ACCOUNT_LOCKED.getCode())
                          .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                          .error(exp.getMessage())
                          .build()
                );
    }
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                                .error(exp.getMessage())
                                .build()
                );

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.RESOURCE_NOT_FOUND.getCode())
                        .businessErrorDescription(BusinessErrorCodes.RESOURCE_NOT_FOUND.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidFileException(InvalidFileException exp) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.INVALID_FILE.getCode())
                        .businessErrorDescription(BusinessErrorCodes.INVALID_FILE.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ExceptionResponse> handleFileTooLargeException(FileTooLargeException exp) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.FILE_TOO_LARGE.getCode())
                        .businessErrorDescription(BusinessErrorCodes.FILE_TOO_LARGE.getDescription())
                        .error(exp.getMessage())
                        .build());
    }





    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .validationErrors(errors)
                        .build());
    }
    @ExceptionHandler(Exception.class)// when user send invalid data
    public ResponseEntity<ExceptionResponse> handleException(Exception exp){

        exp.printStackTrace();// to track where is the exception is happen

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal Error Please Contact With Us")
                                .error(exp.getMessage())
                                .build()
                );

    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleOperationNotPermittedException(
            OperationNotPermittedException exp
    ){

        exp.printStackTrace();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );

    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ExceptionResponse> handleOperationNotPermittedException(
            DuplicatedEmailException exp
    ){


        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("user exist")
                                .error(exp.getMessage())
                                .build()
                );

    }


}
