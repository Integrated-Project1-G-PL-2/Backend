package com.itbangmodkradankanbanapi.exception;

import lombok.Data;

import java.sql.Timestamp;
@Data

public class ItemNotFoundResponse {
        private final Timestamp timestamp;
        private final int status;
        private final  String message;
        private final String instance;
}

