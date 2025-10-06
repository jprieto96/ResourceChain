package com.chainresource.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Storage<T> {

    CompletableFuture<Optional<StorageResult<T>>> read();
    CompletableFuture<Void> write(T value);
    boolean canWrite();

}
