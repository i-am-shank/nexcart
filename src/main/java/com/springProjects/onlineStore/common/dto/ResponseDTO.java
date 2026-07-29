package com.springProjects.onlineStore.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseDTO {
    private Integer status;

    private String message;

    private Object result;

    public ResponseDTO(HttpStatus httpStatus, String message, Object result) {
        this.status = httpStatus.value();
        this.message = message;
        this.result = result;
    }

    public ResponseDTO(HttpStatus httpStatus, String message) {
        this.status = httpStatus.value();
        this.message = message;
    }
}
