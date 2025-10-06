package com.chainresource.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChainResourceTest {

    @Test
    void testChainResourceRequiresNonEmptyChain() {
        assertThrows(IllegalArgumentException.class, () ->
                new ChainResource<String>(null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new ChainResource<String>(new ArrayList<>())
        );
    }

    @Test
    void testGetValueFromSingleStorage() throws ExecutionException, InterruptedException {
        MockStorage<String> storage = new MockStorage<>("test-value", false);
        ChainResource<String> resource = new ChainResource<>(List.of(storage));

        String result = resource.getValue().get();
        assertEquals("test-value", result);
        assertEquals(1, storage.readCount);
    }

    @Test
    void testGetValuePropagatesUpwards() throws ExecutionException, InterruptedException {
        MockStorage<String> storage1 = new MockStorage<>(null, false);
        MockStorage<String> storage2 = new MockStorage<>(null, false);
        MockStorage<String> storage3 = new MockStorage<>("final-value", false);

        ChainResource<String> resource = new ChainResource<>(
                Arrays.asList(storage1, storage2, storage3)
        );

        String result = resource.getValue().get();

        assertEquals("final-value", result);
        assertEquals(1, storage1.readCount);
        assertEquals(1, storage2.readCount);
        assertEquals(1, storage3.readCount);
        assertEquals(1, storage1.writeCount);
        assertEquals(1, storage2.writeCount);
        assertEquals(0, storage3.writeCount); // Read-only doesn't get written
    }

    @Test
    void testExpiredStorageSkipped() throws ExecutionException, InterruptedException {
        MockStorage<String> expiredStorage = new MockStorage<>("expired", true);
        MockStorage<String> validStorage = new MockStorage<>("valid", false);

        ChainResource<String> resource = new ChainResource<>(
                Arrays.asList(expiredStorage, validStorage)
        );

        String result = resource.getValue().get();

        assertEquals("valid", result);
        assertEquals(1, expiredStorage.readCount);
        assertEquals(1, validStorage.readCount);
        assertEquals(1, expiredStorage.writeCount); // Gets updated with fresh value
    }

    @Test
    void testNoStorageCanProvideValue() {
        MockStorage<String> storage1 = new MockStorage<>(null, false);
        MockStorage<String> storage2 = new MockStorage<>(null, false);

        ChainResource<String> resource = new ChainResource<>(
                Arrays.asList(storage1, storage2)
        );

        CompletableFuture<String> future = resource.getValue();

        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    void testReadOnlyStorageNotWritten() throws ExecutionException, InterruptedException {
        MockStorage<String> writableStorage = new MockStorage<>(null, false);
        MockStorage<String> readOnlyStorage = new MockStorage<>("value", false);
        readOnlyStorage.setReadOnly();

        ChainResource<String> resource = new ChainResource<>(
                Arrays.asList(writableStorage, readOnlyStorage)
        );

        resource.getValue().get();

        assertEquals(1, writableStorage.writeCount);
        assertEquals(0, readOnlyStorage.writeCount);
    }

    // Mock Storage for testing
    private static class MockStorage<T> implements Storage<T> {
        private T value;
        private boolean expired;
        private boolean readOnly = false;
        int readCount = 0;
        int writeCount = 0;

        MockStorage(T value, boolean expired) {
            this.value = value;
            this.expired = expired;
        }

        void setReadOnly() {
            this.readOnly = true;
        }

        @Override
        public CompletableFuture<Optional<StorageResult<T>>> read() {
            readCount++;
            if (value == null) {
                return CompletableFuture.completedFuture(Optional.empty());
            }

            Instant timestamp = expired ?
                    Instant.now().minus(Duration.ofHours(2)) :
                    Instant.now();

            StorageResult<T> result = new StorageResult<>(value, timestamp, Duration.ofHours(1));
            return CompletableFuture.completedFuture(Optional.of(result));
        }

        @Override
        public CompletableFuture<Void> write(T value) {
            if (readOnly) {
                throw new UnsupportedOperationException("Read-only storage");
            }
            writeCount++;
            this.value = value;
            this.expired = false;
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public boolean canWrite() {
            return !readOnly;
        }
    }

}
