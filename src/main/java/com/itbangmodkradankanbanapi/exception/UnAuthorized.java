package com.itbangmodkradankanbanapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnAuthorized extends RuntimeException{
    public UnAuthorized(String message) {
        super(message);
    }
}
