package com.shain.demo.concurrentSum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentSum {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Integer>> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            CompletableFuture<Integer> c = CompletableFuture.supplyAsync(() -> {
                int result = 0;
                for (int j = 1; j <= 10; j++) {
                    result += j + finalI*10;
                    try{
                        System.out.println(Thread.currentThread());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return result;
            }, threadPool);
            results.add(c);
        }

        int result = results.stream().map(CompletableFuture::join).reduce(0, Integer::sum);
        System.out.println(result);

        try {
            threadPool.shutdown();
            if (!threadPool.awaitTermination(10, TimeUnit.MINUTES))
                threadPool.shutdownNow();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            threadPool.shutdownNow();
        }
    }
}
