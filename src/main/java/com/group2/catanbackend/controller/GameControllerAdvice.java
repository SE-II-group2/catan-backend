package com.group2.catanbackend.controller;

import com.group2.catanbackend.dto.ErrorResponse;
import com.group2.catanbackend.exception.GameException;
import com.group2.catanbackend.exception.NoSuchGameException;
import com.group2.catanbackend.exception.NoSuchTokenException;
import com.group2.catanbackend.exception.NotAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GameControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(GameException.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(GameException gx){
        log.error(gx.getMessage());

        return new ResponseEntity<>(gx.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return ResponseEntity.status(status).headers(headers).body(
                new ErrorResponse(status.value(), ex.getFieldError().getDefaultMessage() )
        );
    }
    @ExceptionHandler(NoSuchTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ResponseEntity<ErrorResponse> handleNoSuchTokenException(NoSuchTokenException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage())
                );
    }

    @ExceptionHandler(NoSuchGameException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ResponseEntity<ErrorResponse> handleNoSuchGameException(NoSuchGameException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage())
                );
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody ResponseEntity<ErrorResponse> handleNotAuthorizedException(NotAuthorizedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }
}
