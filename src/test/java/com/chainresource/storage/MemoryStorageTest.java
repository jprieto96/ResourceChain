package com.chainresource.storage;

import com.chainresource.core.StorageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemoryStorageTest {

    private MemoryStorage<String> storage;

    @BeforeEach
    void setUp() {
        storage = new MemoryStorage<>(Duration.ofMinutes(30));
    }

    @Test
    void testReadEmptyStorage() throws ExecutionException, InterruptedException {
        Optional<StorageResult<String>> result = storage.read().get();
        assertTrue(result.isEmpty());
    }

    @Test
    void testWriteAndRead() throws ExecutionException, InterruptedException {
        storage.write("test-value").get();

        Optional<StorageResult<String>> result = storage.read().get();
        assertTrue(result.isPresent());
        assertEquals("test-value", result.get().getValue());
    }

    @Test
    void testCanWrite() {
        assertTrue(storage.canWrite());
    }

    @Test
    void testClear() throws ExecutionException, InterruptedException {
        storage.write("test-value").get();
        storage.clear();

        Optional<StorageResult<String>> result = storage.read().get();
        assertTrue(result.isEmpty());
    }

    @Test
    void testOverwrite() throws ExecutionException, InterruptedException {
        storage.write("first-value").get();
        storage.write("second-value").get();

        Optional<StorageResult<String>> result = storage.read().get();
        assertTrue(result.isPresent());
        assertEquals("second-value", result.get().getValue());
    }

}
