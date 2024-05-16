package org.example.javaproject.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyZeroInteractions;

class CacheTest {
    @InjectMocks
    Cache cache;

    @Mock
    private CustomLogger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPutEmptyCache() {
        cache.put("key", "value");
        assertEquals("value", cache.get("key"));
    }

    @Test
    void testPutNotEmptyCache() {
        for (int i = 1; i <= Cache.MAX_SIZE; i++) {
            cache.put("key" + i, "value" + i);
        }
        cache.put("newKey", "newValue");
        assertFalse(cache.contains("key1"));
        assertTrue(cache.contains("newKey"));
        assertEquals("newValue", cache.get("newKey"));
    }

    @Test
    void testRemove() {
        cache.put("key", "value");
        cache.remove("key");
        Object value = cache.get("key");
        assertNull(value);
    }

    @Test
    void contains() {
        cache.put("key1", "value1");
        assertTrue(cache.contains("key1"));
        verifyZeroInteractions(logger);
    }

    @Test
    void clear() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.clear();
        assertFalse(cache.contains("key1"));
        assertFalse(cache.contains("key2"));
    }
}