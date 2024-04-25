package com.itbangmodkradankanbanapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.sql.Timestamp;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final Timestamp timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
}
