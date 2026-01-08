package com.collab.teamservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handle(RuntimeException e) {
    return ResponseEntity.badRequest().body(Map.of(
      "message", e.getMessage()
    ));
  }
}
