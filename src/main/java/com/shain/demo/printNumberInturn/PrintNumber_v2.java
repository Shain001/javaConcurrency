
package com.shain.demo.printNumberInturn;

public class PrintNumber_v2 {

    /**
     * print  numbers in turn using two threads
     *
     */
    private static class Printer implements Runnable {
        private int num = 0;
        private final Object lock = new Object();

        public void run() {
            while (true) {
                synchronized (lock) {
                    // notify the waiting Thread that it's about ready to go
                    // this notify will not have effective for the very first time, since there would be no thread waiting
                    lock.notify();
                    if (num < 100) {
                        System.out.println(Thread.currentThread().getName() + "  " + num++);
                        try {
                            // Make this current wait, effectively giving the other thread a chance to print
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        // Notify the other thread to exit the loop
                        lock.notify();
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Printer p = new Printer();
        Thread t1 = new Thread(p);
        Thread t2 = new Thread(p);

        t1.start();
        t2.start();
    }
}
