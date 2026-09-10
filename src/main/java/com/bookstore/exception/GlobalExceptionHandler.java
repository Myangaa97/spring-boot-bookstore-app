package com.bookstore.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler(ResourceNotfoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotfoundException exception) {
		 ApiError error = new ApiError(LocalDateTime.now(),
				 HttpStatus.NOT_FOUND.value(),
				 HttpStatus.NOT_FOUND.getReasonPhrase(),
				 exception.getMessage(),
				 Map.of()
				 );
		 
		 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException exception) {
		ApiError error = new ApiError(LocalDateTime.now(),
				 HttpStatus.CONFLICT.value(),
				 HttpStatus.CONFLICT.getReasonPhrase(),
				 exception.getMessage(),
				 Map.of()
				 );
		 
		 return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException exception) {
		ApiError error = new ApiError(LocalDateTime.now(),
				 HttpStatus.CONFLICT.value(),
				 HttpStatus.CONFLICT.getReasonPhrase(),
				 exception.getMessage(),
				 Map.of()
				 );
		 
		 return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
}