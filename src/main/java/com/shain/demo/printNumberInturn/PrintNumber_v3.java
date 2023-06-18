package com.shain.demo.printNumberInturn;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * print numbers in turn using three threads, strictly order in A, B, C
 * <p>
 * this class uses retreentLock to implement since it's easier and clearer
 * Using object as a lock can also do the same thing, but it's more inconvenient, since you can't specify which threads
 * to wake up,
 */
public class PrintNumber_v3 {

    private static class Printer implements Runnable {
        private static int num;
        private Lock lock;
        private Condition condition;
        private Condition nextCondition;

        public Printer(ReentrantLock lock, Condition condition, Condition nextCondition) {
            this.lock = lock;
            this.condition = condition;
            this.nextCondition = nextCondition;
        }


        @Override
        public void run() {
            try {
                while (num < 30) {
                    lock.lock();
                    // 释放所， 进入await 状态
                    condition.await();
                    // 如果将 lock.lock写在while 循环外，则这里需要重复判断 num是否大于30， 否则会打印出31。 因为所有线程都在line35出await， 这意味着他们已经进入了while循环，
                    // 所以就算此时num已经大于了29， 还是会打印出30/31
                    System.out.println(Thread.currentThread().getName() + " " + num++);
                    nextCondition.signal();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 程序启动时， ABC均会进入 printer的run()， 并且会开始争抢lock。
     * at the very first time, 不一定是ABC中的哪个获得锁， 但是无论是哪一个获得了锁， 由于有condition.await()的存在，
     * 他都会让自身休眠。也即， 在程序刚刚开始， 走到 main 中的conditionA.signal之前， 没有线程能够执行run（）
     * <p>
     * 之后， 在main 中的 lock.lock, 也不一定是哪个线程获得这个锁， 但是不论谁获得， 都通知A线程被唤醒。 此时，所有线程都在run方法中await，
     * A被唤醒之后则可开始执行， 然后依次唤醒下一个线程。
     *
     * @param args
     */
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
            // Signal the first condition to allow threadA to start executing
            conditionA.signal();
        } finally {
            lock.unlock();
        }
    }
}
