package com.shain.demo.concurrentSum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ConcurrentSum {
    private static class Sum {
        private int i;

        public Sum(int i) {
            this.i = i;
        }


        public Integer doSum() {
            int sum = 0;

            for (int j = 1; j <= 10; j++) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                sum += j + i * 10;
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Integer>> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            CompletableFuture<Integer> c = CompletableFuture.supplyAsync(() -> new Sum(finalI).doSum(), threadPool);
            results.add(c);
        }

        int result = results.stream().map(CompletableFuture::join).reduce(0, Integer::sum);
        System.out.println(result);
    }
}
