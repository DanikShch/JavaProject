package org.example.javaproject.controller;

import org.example.javaproject.component.CustomLogger;
import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.dto.RequestDTO;
import org.example.javaproject.service.CounterService;
import org.example.javaproject.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.example.javaproject.controller.RequestController.SUCCESS_MSG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestControllerTest {
    @InjectMocks
    private RequestController requestController;

    @Mock
    private RequestService requestService;

    @Mock
    private CustomLogger logger;

    @Mock
    private CounterService counterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void extractEmails() {
        String text = "Txt with emails example@example.com and example@gmail.com";
        List<EmailDTO> expectedEmails = Arrays.asList(
                new EmailDTO("example@example.com"),
                new EmailDTO("example@gmail.com")
        );
        when(requestService.extractEmails(text)).thenReturn(expectedEmails);
        ResponseEntity<List<EmailDTO>> response = requestController.extractEmails(text);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmails, response.getBody());
        verify(requestService).extractEmails(text);
    }

    @Test
    void extractNumbers() {
        String text = "Txt with numbers 1234567890 and 0987654321";
        List<NumberDTO> expectedNumbers = Arrays.asList(
                new NumberDTO("1234567890"),
                new NumberDTO("0987654321")
        );
        when(requestService.extractPhoneNumbers(text)).thenReturn(expectedNumbers);
        ResponseEntity<List<NumberDTO>> response = requestController.extractNumbers(text);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNumbers, response.getBody());
        verify(requestService).extractPhoneNumbers(text);
    }

    @Test
    void getRequests() {
        List<RequestDTO> expectedRequests = Arrays.asList(
                new RequestDTO("Request 1"),
                new RequestDTO("Request 2")
        );
        when(requestService.getRequests(null)).thenReturn(expectedRequests);
        ResponseEntity<List<RequestDTO>> response = requestController.getRequests(null);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRequests, response.getBody());
        verify(requestService).getRequests(null);
    }

    @Test
    void updateRequest() {
        String request = "Request";
        String newRequest = "New Request";

        ResponseEntity<MessageDTO> response = requestController.updateRequest(request, newRequest);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(requestService).updateRequest(request, newRequest);

    }

    @Test
    void deleteRequest() {
        String request = "Request";
        ResponseEntity<MessageDTO> response = requestController.deleteRequest(request);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(requestService).deleteRequest(request);
    }
}