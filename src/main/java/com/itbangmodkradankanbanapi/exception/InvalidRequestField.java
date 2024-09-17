package com.itbangmodkradankanbanapi.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class InvalidRequestField extends ResponseStatusException {
    public InvalidRequestField(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
