package com.example.producerconsumerdemo;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueDemo {
    private static final String TAG = "BlockingQueueDemo";
    private final BlockingQueue blockingQueue = new ArrayBlockingQueue<>(4);

    private int count = 0;

    class Producer implements Runnable {

        @Override
        public void run() {

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                blockingQueue.put(1);
                count++;
                Log.d(TAG, Thread.currentThread().getName() + " producer count=" + count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                blockingQueue.take();
                count--;
                Log.d(TAG, Thread.currentThread().getName() + "_consumer count=" + count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
