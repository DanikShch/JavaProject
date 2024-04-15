package org.example.javaproject.service;

import org.example.javaproject.component.Cache;
import org.example.javaproject.dto.NumberDTO;
import org.example.javaproject.entity.Number;
import org.example.javaproject.exceptions.ServiceException;
import org.example.javaproject.repository.NumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NumberServiceTest {
    @Mock
    Cache cache;
    @Mock
    NumberRepository numberRepository;
    @InjectMocks
    NumberService numberService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddValidNumber() {
        String number = "123 4567890";
        when(numberRepository.findByName(number)).thenReturn(null);
        numberService.addNumber(number);
        verify(numberRepository).findByName(number);
        verify(numberRepository).save(any(Number.class));
        verify(cache).put(eq(number), any(Number.class));
    }

    @Test
    void testAddInvalidNumber() {
        String number = "Not a number";
        assertThrows(ServiceException.class, () -> numberService.addNumber(number));
        verify(numberRepository, never()).findByName(number);
        verify(numberRepository, never()).save(any(Number.class));
        verify(cache, never()).put(eq(number), any(Number.class));
    }

    @Test
    void testAddExistingNumber() {
        String number = "123 4567890";
        when(numberRepository.findByName(number)).thenReturn(new Number(number));
        assertThrows(ServiceException.class, () -> numberService.addNumber(number));
        verify(numberRepository).findByName(number);
        verify(numberRepository, never()).save(any(Number.class));
        verify(cache, never()).put(eq(number), any(Number.class));
    }

    @Test
    void deleteExistingNumber() {
        String number = "1234567890";
        Number numberEntity = new Number(number);
        when(numberRepository.findByName(number)).thenReturn(numberEntity);
        numberService.deleteNumber(number);
        verify(numberRepository).findByName(number);
        verify(numberRepository).delete(numberEntity);
        verify(cache).remove(number);
    }

    @Test
    void deleteNonExistingNumber() {
        String number = "1234567890";
        when(numberRepository.findByName(number)).thenReturn(null);
        assertThrows(ServiceException.class, () -> numberService.deleteNumber(number));
        verify(numberRepository).findByName(number);
        verify(numberRepository, never()).delete(any(Number.class));
        verify(cache, never()).remove(number);
    }

    @Test
    void updateNumberAlreadyExist() {
        String number = "123";
        String newNumber = "456";
        Number numberEntity = new Number(number);
        when(numberRepository.findByName(number)).thenReturn(numberEntity);
        when(numberRepository.findByName(newNumber)).thenReturn(new Number(newNumber));
        assertThrows(ServiceException.class, () -> numberService.updateNumber(number, newNumber));
        assertEquals(number, numberEntity.getName());
        verify(numberRepository).findByName(number);
        verify(cache, never()).remove(anyString());
        verify(cache, never()).put(anyString(), any(Number.class));
    }

    @Test
    void updateNumberValidNumbers() {
        String number = "12345";
        String newNumber = "123 4567890";
        Number numberEntity = new Number(number);
        when(numberRepository.findByName(number)).thenReturn(numberEntity);
        when(numberRepository.findByName(newNumber)).thenReturn(null);
        numberService.updateNumber(number, newNumber);
        assertEquals(newNumber, numberEntity.getName());
        verify(numberRepository).findByName(number);
        verify(numberRepository).findByName(newNumber);
        verify(cache).remove(number);
        verify(cache).put(newNumber, numberEntity);
    }

    @Test
    void updateNumberInvalidNewNumber() {
        String number = "123";
        String newNumber = "Not number";
        Number numberEntity = new Number(number);
        when(numberRepository.findByName(number)).thenReturn(numberEntity);
        assertThrows(ServiceException.class, () -> numberService.updateNumber(number, newNumber));
        assertEquals(number, numberEntity.getName());
        verify(numberRepository).findByName(number);
        verify(numberRepository, never()).findByName(newNumber);
        verify(cache, never()).remove(anyString());
        verify(cache, never()).put(anyString(), any(Number.class));
    }

    @Test
    void updateNumberInvalidOldNumber() {
        String number = "123";
        String newNumber = "456";
        when(numberRepository.findByName(number)).thenReturn(null);
        assertThrows(ServiceException.class, () -> numberService.updateNumber(number, newNumber));
        verify(numberRepository).findByName(number);
        verify(numberRepository, never()).findByName(newNumber);
        verify(cache, never()).remove(anyString());
        verify(cache, never()).put(anyString(), any(Number.class));
    }



    @Test
    void testGetNumbers() {
        Number number1 = new Number("123");
        Number number2 = new Number("456");
        List<Number> numberEntities = Arrays.asList(number1, number2);
        when(numberRepository.findAll()).thenReturn(numberEntities);
        List<NumberDTO> result = numberService.getNumbers();
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("123", result.get(0).getNumber());
        assertEquals("456", result.get(1).getNumber());
        verify(numberRepository).findAll();
        verify(cache).put("123", number1);
        verify(cache).put("456", number2);
    }

    @Test
    void testGetNumbersNotExists() {
        when(numberRepository.findAll()).thenReturn(new ArrayList<>());
        List<NumberDTO> result = numberService.getNumbers();
        assertTrue(result.isEmpty());
        verify(numberRepository).findAll();
        verify(cache, never()).put(anyString(), any(Number.class));
    }
}