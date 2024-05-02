package com.itbangmodkradankanbanapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ItemNotFoundForUpdateAndDelete extends RuntimeException{
    public ItemNotFoundForUpdateAndDelete(String message){
        super(message);
    }
}

