package com.example.producerconsumerdemo;

import android.util.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    private static final String TAG = "ReentrantLockDemo";
    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private int count = 0;
    private static final int FULL = 4;

    class Producer implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lock.lock();
            try {
                while (count == FULL) {
                    try {
                        notFull.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                Log.d(TAG, Thread.currentThread().getName() + " producer count=" + count);
                //通知消费者
                notEmpty.signal();
            } finally {
                lock.unlock();
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
            lock.lock();
            try {
                while (count == 0) {
                    try {
                        notEmpty.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count--;
                Log.d(TAG, Thread.currentThread().getName() + "_consumer count=" + count);
                notFull.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}
