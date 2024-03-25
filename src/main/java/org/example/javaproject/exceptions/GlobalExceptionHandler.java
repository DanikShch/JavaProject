package org.example.javaproject.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.example.javaproject.component.CustomLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final CustomLogger logger;

    public GlobalExceptionHandler(CustomLogger logger) {
        this.logger = logger;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handlerInternalServerError(RuntimeException ex) {
        logger.error("error, 500 code");
        return new ErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpClientErrorException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
    MissingServletRequestParameterException.class, ConstraintViolationException.class})
    public ErrorResponse handlerBadRequestException(Exception ex) {
        logger.error("error, 400 code");
        return new ErrorResponse("400 error, BAD REQUEST");
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handlerMethodNotAllowed(Exception ex) {
        logger.error("error, 405 code");
        return new ErrorResponse("405 error, METHOD NOT ALLOWED");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse handlerFoundException(Exception ex) {
        logger.error("error, 404 code");
        return new ErrorResponse("404 error, NOT FOUND");
    }
}
