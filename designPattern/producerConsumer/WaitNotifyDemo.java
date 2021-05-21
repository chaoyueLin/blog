package com.example.producerconsumerdemo;

import android.util.Log;

public class WaitNotifyDemo {
    private static final String TAG = "WaitNotifyDemo";
    private static final String LOCK = "lock";
    private static final int FULL = 4;
    private int count;

    class Producer implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized (LOCK) {
                while (count == FULL) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                Log.d(TAG, Thread.currentThread().getName() + " producer count=" + count);
                LOCK.notifyAll();
            }
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (LOCK) {
                while (count == 0) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count--;
                Log.d(TAG, Thread.currentThread().getName() + "_consumer count=" + count);
                LOCK.notifyAll();
            }
        }
    }


}
