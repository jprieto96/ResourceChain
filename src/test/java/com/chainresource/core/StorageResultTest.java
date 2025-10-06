package com.chainresource.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class StorageResultTest {

    @Test
    void testIsNotExpired() {
        StorageResult<String> result = new StorageResult<>(
                "test",
                Instant.now(),
                Duration.ofMinutes(30)
        );

        assertFalse(result.isExpired());
    }

    @Test
    void testIsExpired() {
        Instant pastTimestamp = Instant.now().minus(Duration.ofHours(1));
        StorageResult<String> result = new StorageResult<>(
                "test",
                pastTimestamp,
                Duration.ofMinutes(30)
        );

        assertTrue(result.isExpired());
    }

    @Test
    void testIsExpiredAt() {
        Instant timestamp = Instant.now();
        StorageResult<String> result = new StorageResult<>(
                "test",
                timestamp,
                Duration.ofMinutes(30)
        );

        Instant checkTime = timestamp.plus(Duration.ofMinutes(31));
        assertTrue(result.isExpiredAt(checkTime));

        checkTime = timestamp.plus(Duration.ofMinutes(29));
        assertFalse(result.isExpiredAt(checkTime));
    }

}
