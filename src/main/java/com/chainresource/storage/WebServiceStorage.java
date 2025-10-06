package com.chainresource.storage;

import com.chainresource.core.Storage;
import com.chainresource.core.StorageResult;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WebServiceStorage<T> implements Storage<T> {

    private final String apiUrl;
    private final Class<T> type;
    private final HttpClient httpClient;
    private final Gson gson;

    public WebServiceStorage(String apiUrl, Class<T> type) {
        this.apiUrl = apiUrl;
        this.type = type;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    @Override
    public CompletableFuture<Optional<StorageResult<T>>> read() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        T value = gson.fromJson(response.body(), type);
                        System.out.println("[WebServiceStorage] Fetched from API");
                        // Web service data is always "fresh"
                        StorageResult<T> result = new StorageResult<>(
                                value,
                                Instant.now(),
                                Duration.ofDays(365)
                        );
                        return Optional.of(result);
                    } else {
                        System.err.println("[WebServiceStorage] API error: " + response.statusCode());
                        return Optional.<StorageResult<T>>empty();
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("[WebServiceStorage] Exception: " + ex.getMessage());
                    return Optional.empty();
                });
    }

    @Override
    public CompletableFuture<Void> write(T value) {
        throw new UnsupportedOperationException("WebServiceStorage is read-only");
    }

    @Override
    public boolean canWrite() {
        return false;
    }

}
