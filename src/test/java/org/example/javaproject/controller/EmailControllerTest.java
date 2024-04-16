package org.example.javaproject.controller;

import org.example.javaproject.dto.EmailDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.javaproject.controller.EmailController.SUCCESS_MSG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerTest {
    @InjectMocks
    private EmailController emailController;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddEmail() {
        String email = "test@example.com";
        ResponseEntity<MessageDTO> response = emailController.addEmail(email);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(emailService).addEmail(email);
    }

    @Test
    void testGetAllEmails() {
        List<EmailDTO> expectedEmails = Arrays.asList(
                new EmailDTO("test1@example.com"),
                new EmailDTO("test2@example.com"),
                new EmailDTO("test3@exa.com")
        );
        when(emailService.getEmails(null)).thenReturn(expectedEmails);
        ResponseEntity<List<EmailDTO>> response = emailController.getEmails(null);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmails, response.getBody());
        verify(emailService).getEmails(null);
    }

    @Test
    void testGetEmailsWithDomain() {
        String domain = "@example.com";
        List<EmailDTO> expectedEmails = Arrays.asList(
                new EmailDTO("test1@example.com"),
                new EmailDTO("test2@example.com")
        );
        when(emailService.getEmails(domain)).thenReturn(expectedEmails);
        ResponseEntity<List<EmailDTO>> response = emailController.getEmails(domain);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedEmails, response.getBody());
        verify(emailService).getEmails(domain);
    }

    @Test
    void testUpdateEmail() {
        String email = "test@example.com";
        String newEmail = "newtest@example.com";
        ResponseEntity<MessageDTO> response = emailController.updateEmail(email, newEmail);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(emailService).updateEmail(email, newEmail);
    }

    @Test
    void testDeleteEmail() {
        String email = "test@example.com";
        ResponseEntity<MessageDTO> response = emailController.deleteEmail(email);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(emailService).deleteEmail(email);
    }

    @Test
    public void testAddEmails() {
        List<EmailDTO> emails = new ArrayList<>();
        emails.add(new EmailDTO("example1@example.com"));
        emails.add(new EmailDTO("example2@example.com"));
        emails.add(new EmailDTO("example3@example.com"));

        ResponseEntity<MessageDTO> response = emailController.addEmails(emails);
        verify(emailService, times(1)).addEmails(emails);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
    }


}