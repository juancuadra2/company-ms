package com.jcuadrado.company.advices;

import com.jcuadrado.company.exceptions.ErrorResponse;
import com.jcuadrado.company.exceptions.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneralControllerAdviceTest {

    @InjectMocks
    private GeneralControllerAdvice generalControllerAdvice;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Test
    @DisplayName("Test handleGeneralException with BAD_REQUEST")
    void testHandleGeneralExceptionWithBadRequest() {
        GeneralException exception = new GeneralException(HttpStatus.BAD_REQUEST, "Test error message");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleGeneralException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Test error message", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Test handleGeneralException with UNAUTHORIZED")
    void testHandleGeneralExceptionWithUnauthorized() {
        GeneralException exception = new GeneralException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleGeneralException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Authentication failed", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test handleGeneralException with INTERNAL_SERVER_ERROR")
    void testHandleGeneralExceptionWithInternalServerError() {
        GeneralException exception = new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleGeneralException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Internal error occurred", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test handleValidationException with single field error")
    void testHandleValidationExceptionWithSingleFieldError() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "Field is required");
        List<FieldError> fieldErrors = Collections.singletonList(fieldError);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleValidationException(methodArgumentNotValidException);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Error", response.getBody().getError());
        assertEquals("Error de validación en los campos de la solicitud", response.getBody().getMessage());
        assertNotNull(response.getBody().getDetails());
        assertEquals("Field is required", response.getBody().getDetails().get("fieldName"));
    }

    @Test
    @DisplayName("Test handleValidationException with multiple field errors")
    void testHandleValidationExceptionWithMultipleFieldErrors() {
        FieldError fieldError1 = new FieldError("objectName", "field1", "Field 1 is required");
        FieldError fieldError2 = new FieldError("objectName", "field2", "Field 2 is invalid");
        List<FieldError> fieldErrors = List.of(fieldError1, fieldError2);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleValidationException(methodArgumentNotValidException);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Error", response.getBody().getError());
        assertNotNull(response.getBody().getDetails());
        assertEquals(2, response.getBody().getDetails().size());
        assertEquals("Field 1 is required", response.getBody().getDetails().get("field1"));
        assertEquals("Field 2 is invalid", response.getBody().getDetails().get("field2"));
    }

    @Test
    @DisplayName("Test handleValidationException with field error having null message")
    void testHandleValidationExceptionWithNullMessage() {
        FieldError fieldError = new FieldError("objectName", "fieldName", null);
        List<FieldError> fieldErrors = Collections.singletonList(fieldError);
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleValidationException(methodArgumentNotValidException);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getDetails());
        assertEquals("Error de validación", response.getBody().getDetails().get("fieldName"));
    }

    @Test
    @DisplayName("Test handleAuthenticationException")
    void testHandleAuthenticationException() {
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn("Bad credentials");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleAuthenticationException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Bad credentials", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test handleAccessDeniedException")
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleAccessDeniedException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden", response.getBody().getError());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test handleHttpRequestMethodNotSupportedException")
    void testHandleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleHttpRequestMethodNotSupportedException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(405, response.getBody().getStatus());
        assertEquals("Method Not Allowed", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("POST"));
    }

    @Test
    @DisplayName("Test handleHttpMediaTypeNotSupportedException")
    void testHandleHttpMediaTypeNotSupportedException() {
        HttpMediaTypeNotSupportedException exception = new HttpMediaTypeNotSupportedException("application/xml");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleHttpMediaTypeNotSupportedException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(415, response.getBody().getStatus());
        assertEquals("Unsupported Media Type", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("application/xml"));
    }

    @Test
    @DisplayName("Test handleHttpMessageNotReadableException")
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleHttpMessageNotReadableException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("El cuerpo de la petición es requerido", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test handleNoResourceFoundException")
    void testHandleNoResourceFoundException() {
        NoResourceFoundException exception = new NoResourceFoundException(null, "/api/test");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleNoResourceFoundException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource Not Found", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("/api/test"));
    }

    @Test
    @DisplayName("Test handleException")
    void testHandleException() {
        Exception exception = new RuntimeException("Unexpected error");
        ResponseEntity<ErrorResponse> response = generalControllerAdvice.handleException(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Unexpected error", response.getBody().getMessage());
    }
}
