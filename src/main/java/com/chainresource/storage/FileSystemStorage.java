package com.chainresource.storage;

import com.chainresource.core.Storage;
import com.chainresource.core.StorageResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FileSystemStorage<T> implements Storage<T> {

    private final String filePath;
    private final Duration expiration;
    private final Class<T> type;
    private final Gson gson;

    public FileSystemStorage(String filePath, Duration expiration, Class<T> type) {
        this.filePath = filePath;
        this.expiration = expiration;
        this.type = type;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public CompletableFuture<Optional<StorageResult<T>>> read() {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(filePath);
            if (!file.exists()) {
                return Optional.empty();
            }

            try (Reader reader = new FileReader(file)) {
                CachedData<T> cached = gson.fromJson(reader,
                        com.google.gson.reflect.TypeToken.getParameterized(
                                CachedData.class, type).getType());

                if (cached != null && cached.value != null) {
                    StorageResult<T> result = new StorageResult<>(
                            cached.value,
                            Instant.ofEpochMilli(cached.timestamp),
                            expiration
                    );
                    System.out.println("[FileSystemStorage] Read from file");
                    return Optional.of(result);
                }
            } catch (IOException e) {
                System.err.println("[FileSystemStorage] Error reading: " + e.getMessage());
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> write(T value) {
        return CompletableFuture.runAsync(() -> {
            CachedData<T> cached = new CachedData<>(value, System.currentTimeMillis());

            // Ensure parent directory exists
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (Writer writer = new FileWriter(filePath)) {
                gson.toJson(cached, writer);
                writer.flush(); // Ensure data is written to disk
                System.out.println("[FileSystemStorage] Written to file: " + filePath);
            } catch (IOException e) {
                System.err.println("[FileSystemStorage] Error writing: " + e.getMessage());
                throw new RuntimeException("Failed to write to file", e);
            }
        });
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    // For testing
    public void deleteFile() {
        new File(filePath).delete();
    }

    private static class CachedData<T> {
        T value;
        long timestamp;

        CachedData(T value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

}
