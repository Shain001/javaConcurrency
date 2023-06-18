package com.shain.demo.printNumberInturn;

public class PrintNumber {

    /**
     * Output look like this:
     * Thread-0  0
     * Thread-0  1
     * Thread-0  2
     * Thread-0  3
     * Thread-1  4
     * Thread-1  5
     * Thread-1  6
     * Thread-1  7
     *
     * Not in turn
     */
    private static class Printer implements Runnable {
        private int num = 0;

        public void run() {
            while (num < 100)
                print();
        }

        private synchronized void print() {
            if (num < 100) {
                System.out.println(Thread.currentThread().getName() + "  " + num++);
            }
        }

        public int getNum() {
            return num;
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
