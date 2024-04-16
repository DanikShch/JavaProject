package org.example.javaproject.controller;

import org.example.javaproject.dto.MessageDTO;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.service.NumberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.example.javaproject.controller.NumberController.SUCCESS_MSG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NumberControllerTest {
    @InjectMocks
    private NumberController numberController;

    @Mock
    private NumberService numberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddNumber() {
        String number = "1234567890";
        ResponseEntity<MessageDTO> response = numberController.addNumber(number);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(numberService).addNumber(number);
    }

    @Test
    void testGetNumbers() {
        List<NumberDTO> expectedNumbers = Arrays.asList(
                new NumberDTO("1234567890"),
                new NumberDTO("9876543210")
        );
        when(numberService.getNumbers()).thenReturn(expectedNumbers);
        ResponseEntity<List<NumberDTO>> response = numberController.getNumbers();
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNumbers, response.getBody());
        verify(numberService).getNumbers();
    }

    @Test
    void testUpdateNumber() {
        String number = "1234567890";
        String newNumber = "0987654321";
        ResponseEntity<MessageDTO> response = numberController.updateNumber(number, newNumber);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(numberService).updateNumber(number, newNumber);
    }

    @Test
    void testDeleteNumber() {
        String number = "1234567890";
        ResponseEntity<MessageDTO> response = numberController.deleteNumber(number);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MSG, response.getBody().getMessage());
        verify(numberService).deleteNumber(number);
    }
}