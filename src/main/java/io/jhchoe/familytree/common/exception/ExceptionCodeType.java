package io.jhchoe.familytree.common.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionCodeType {

    String getCode();

    String getMessage();

    HttpStatus getStatus();
}
