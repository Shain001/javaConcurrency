package com.shain.demo.deadlock;

import java.util.concurrent.TimeUnit;

public class DeadLockDemo {
    public static void main(String[] args) {
        final Object lockA = new Object();
        final Object lockB = new Object();

        new Thread(() -> {
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + " Got Lock A, try get LockB now");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (lockB) {
                    System.out.println(Thread.currentThread().getName() + " Got Lock A and LockB now");
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + " Got Lock B, try get LockA now");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (lockA) {
                    System.out.println(Thread.currentThread().getName() + " Got Lock A and LockB now");
                }
            }
        }).start();
    }
}
