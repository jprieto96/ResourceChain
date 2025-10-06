package org.example;

public class ChainResource<T> {

    private final List<Storage<T>> storageChain;

    public ChainResource(List<Storage<T>> storageChain) {
        if (storageChain == null || storageChain.isEmpty()) {
            throw new IllegalArgumentException("Storage chain cannot be empty");
        }
        this.storageChain = new ArrayList<>(storageChain);
    }

    /**
     * Gets the value by traversing the chain, propagating upwards on success
     */
    public CompletableFuture<T> getValue() {
        return getValueFromChain(0);
    }

    private CompletableFuture<T> getValueFromChain(int index) {
        if (index >= storageChain.size()) {
            return CompletableFuture.failedFuture(
                    new RuntimeException("No storage in chain could provide value")
            );
        }

        Storage<T> storage = storageChain.get(index);

        return storage.read().thenCompose(result -> {
            if (result.isPresent() && !result.get().isExpired()) {
                // Found valid data, propagate upwards
                T value = result.get().getValue();
                propagateUpwards(index - 1, value);
                return CompletableFuture.completedFuture(value);
            } else {
                // Try next storage in chain
                return getValueFromChain(index + 1);
            }
        });
    }

    private void propagateUpwards(int toIndex, T value) {
        for (int i = toIndex; i >= 0; i--) {
            Storage<T> storage = storageChain.get(i);
            if (storage.canWrite()) {
                storage.write(value);
            }
        }
    }

}
