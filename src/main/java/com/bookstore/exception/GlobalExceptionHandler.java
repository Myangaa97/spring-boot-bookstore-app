package com.bookstore.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		ApiError error = new ApiError(LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Validation failed",
				fieldErrors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	
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
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiError> handleRuntimeException(RuntimeException exception) {
		log.error("Unhandled exception", exception);
		ApiError error = new ApiError(LocalDateTime.now(),
				 HttpStatus.INTERNAL_SERVER_ERROR.value(),
				 HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				 "An unexpected error occurred",
				 Map.of()
				 );
		 
		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	
}