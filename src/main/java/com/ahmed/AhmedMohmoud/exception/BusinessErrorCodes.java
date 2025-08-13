package com.ahmed.AhmedMohmoud.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or Password is incorrect"),
    RESOURCE_NOT_FOUND(305, NOT_FOUND, "Requested resource not found"), // New
    OPERATION_NOT_PERMITTED(306, FORBIDDEN, "Operation not permitted"),  // New
    INVALID_FILE(307, BAD_REQUEST, "Invalid file provided"),             // New
    FILE_TOO_LARGE(308, BAD_REQUEST, "File exceeds size limit"),
    ;



    private final int code;
    private final HttpStatus htpStatus;
    private final String description;
    BusinessErrorCodes(int code , HttpStatus htpStatus , String description){
        this.code=code;
        this.htpStatus=htpStatus;
        this.description=description;
    }

}
