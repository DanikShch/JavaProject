package org.example.javaproject.controller;

import org.example.javaproject.dto.DomainDTO;
import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.service.EmailTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.example.javaproject.controller.EmailTypeController.SUCCESS_MSG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailTypeControllerTest {
    @InjectMocks
    private EmailTypeController emailTypeController;

    @Mock
    private EmailTypeService emailTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddDomain() {
        String domain = "@example.com";
        ResponseEntity<MessageDTO> response = emailTypeController.addDomain(domain);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(emailTypeService).addDomain(domain);
    }

    @Test
    void testGetDomains() {
        List<DomainDTO> expectedDomains = Arrays.asList(
                new DomainDTO("@example1.com"),
                new DomainDTO("@example2.com")
        );
        when(emailTypeService.getDomains()).thenReturn(expectedDomains);
        ResponseEntity<List<DomainDTO>> response = emailTypeController.getDomains();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDomains, response.getBody());
        verify(emailTypeService).getDomains();

    }

    @Test
    void testUpdateDomain() {
        String domain = "@example.com";
        String newDomain = "@newexample.com";
        ResponseEntity<MessageDTO> response = emailTypeController.updateDomain(domain, newDomain);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(emailTypeService).updateDomain(domain, newDomain);
    }

    @Test
    void deleteDomain() {
        String domain = "example.com";
        ResponseEntity<MessageDTO> response = emailTypeController.deleteDomain(domain);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(emailTypeService).deleteDomain(domain);
    }
}