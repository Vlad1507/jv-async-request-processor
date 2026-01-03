package mate.academy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class AsyncRequestProcessor {
    private final Executor executor;
    private ConcurrentHashMap<String, CompletableFuture<UserData>> cache =
            new ConcurrentHashMap<>();

    public AsyncRequestProcessor(Executor executor) {
        this.executor = executor;
    }

    public CompletableFuture<UserData> processRequest(String userId) {
        return cache.computeIfAbsent(userId,
                key -> CompletableFuture
                        .supplyAsync(() -> getUserData(key), executor)
                        .whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                cache.remove(key);
                            }
                        })
        );
    }

    private static UserData getUserData(String key) {
        try {
            Thread.sleep(200);
            return new UserData(key, "Details for " + key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        }
    }
}
