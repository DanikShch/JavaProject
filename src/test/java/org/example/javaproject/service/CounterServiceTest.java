package org.example.javaproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

class CounterServiceTest {
    @InjectMocks
    private CounterService counterService;

    @BeforeEach
    void setUp() {
        counterService = new CounterService();
    }

    @Test
    void testIncrementAngGet() {
        int currentValue = counterService.incrementAndGet();
        assertEquals(1, currentValue);
    }

    @Test
    void testGet() {
        int currentValue = counterService.get();
        assertEquals(0, currentValue);
    }
}