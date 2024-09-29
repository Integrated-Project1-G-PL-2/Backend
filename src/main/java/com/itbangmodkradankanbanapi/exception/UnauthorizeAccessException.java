package com.itbangmodkradankanbanapi.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizeAccessException extends ResponseStatusException {
    public UnauthorizeAccessException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
