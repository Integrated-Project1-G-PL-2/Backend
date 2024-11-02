package com.itbangmodkradankanbanapi.db1.v3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class MailResponse {
    String status;
    String message;
}
