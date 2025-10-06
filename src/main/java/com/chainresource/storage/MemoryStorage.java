package com.chainresource.storage;

import com.chainresource.core.Storage;
import com.chainresource.core.StorageResult;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MemoryStorage<T> implements Storage<T> {

    private T cachedValue;
    private Instant cacheTimestamp;
    private final Duration expiration;

    public MemoryStorage(Duration expiration) {
        this.expiration = expiration;
    }

    @Override
    public CompletableFuture<Optional<StorageResult<T>>> read() {
        if (cachedValue != null && cacheTimestamp != null) {
            StorageResult<T> result = new StorageResult<>(cachedValue, cacheTimestamp, expiration);
            return CompletableFuture.completedFuture(Optional.of(result));
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Void> write(T value) {
        this.cachedValue = value;
        this.cacheTimestamp = Instant.now();
        System.out.println("[MemoryStorage] Cached value");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    // For testing
    public void clear() {
        this.cachedValue = null;
        this.cacheTimestamp = null;
    }

}
