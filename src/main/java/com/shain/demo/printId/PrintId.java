package com.shain.demo.printId;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PrintId {
    private static class Printer implements Runnable{
        private ReentrantLock lock;
        private Condition condition;
        private Condition nexCondition;
        private int count = 10;

        public Printer(ReentrantLock lock, Condition condition, Condition nexCondition) {
            this.lock = lock;
            this.condition = condition;
            this.nexCondition = nexCondition;
        }

        @Override
        public void run() {
            for (int c = 0; c < count; c++) {
                lock.lock();

                try {
                    condition.await();
                    for (int i = 0; i < 5; i++) {
                        System.out.println(Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                    }

                    nexCondition.signal();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Condition conditionA = lock.newCondition();
        Condition conditionB = lock.newCondition();
        Condition conditionC = lock.newCondition();

        Thread threadA = new Thread(new Printer(lock, conditionA, conditionB));
        Thread threadB = new Thread(new Printer(lock, conditionB, conditionC));
        Thread threadC = new Thread(new Printer(lock, conditionC, conditionA));

        threadA.start();
        threadB.start();
        threadC.start();

        lock.lock();
        try {
            conditionA.signal();
        } finally {
            lock.unlock();
        }
    }
}
