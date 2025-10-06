package com.chainresource.core;

import java.time.Duration;
import java.time.Instant;

public class StorageResult<T> {

    private T value;
    private Instant timestamp;
    private Duration expiration;

    public StorageResult(T value, Instant timestamp, Duration expiration) {
        this.value = value;
        this.timestamp = timestamp;
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(timestamp.plus(expiration));
    }

    public boolean isExpiredAt(Instant checkTime) {
        return checkTime.isAfter(timestamp.plus(expiration));
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Duration getExpiration() {
        return expiration;
    }

    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }
}
