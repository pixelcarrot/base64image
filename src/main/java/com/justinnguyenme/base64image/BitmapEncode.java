package com.justinnguyenme.base64image;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/16/16.
 */
public class BitmapEncode implements Runnable {

    // Constants for indicating the state of the encode
    static final int STATE_FAILED = -1;
    static final int STATE_STARTED = 0;
    static final int STATE_COMPLETED = 1;
    private static final String TAG = BitmapEncode.class.getSimpleName();
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();
    // Limits the number of times the encoder tries to process an image
    private static final int NUMBER_OF_TRIES = 3;

    // Tells the Runnable to pause for a certain number of milliseconds
    private static final long SLEEP_TIME_MILLISECONDS = 500;
    final int sequence;
    final Bitmap mBitmap;
    final TaskRunnableEncode mTask;

    public BitmapEncode(Bitmap bitmap, TaskRunnableEncode task) {
        sequence = SEQUENCE_GENERATOR.incrementAndGet();
        mTask = task;
        mBitmap = bitmap;
    }

    @Override
    public void run() {

        mTask.setEncodeThread(Thread.currentThread());

        String base64 = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {

            mTask.handleEncodeState(STATE_STARTED);

            if (Thread.interrupted()) {
                return;
            }

            for (int i = 0; i < NUMBER_OF_TRIES; i++) {

                try {
                    Thread.sleep(SLEEP_TIME_MILLISECONDS);
                } catch (java.lang.InterruptedException interruptException) {
                    return;
                }

                try {
                    Log.d(TAG, "Thread " + sequence + ": Encoding");
                    base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    break;

                } catch (Throwable e) {
                    Log.e(TAG, "Thread " + sequence + ": Out of memory in encode stage.");

                    /*
                     * Tells the system that garbage collection is
                     * necessary. Notice that collection may or may not
                     * occur.
                     */
                    java.lang.System.gc();

                    if (Thread.interrupted()) {
                        return;
                    }
                    /*
                     * Tries to pause the thread for 250 milliseconds,
                     * and catches an Exception if something tries to
                     * activate the thread before it wakes up.
                     */
                    try {
                        Thread.sleep(SLEEP_TIME_MILLISECONDS);
                    } catch (java.lang.InterruptedException interruptException) {
                        return;
                    }
                }
            }

            // Catches exceptions if something tries to activate the
            // Thread incorrectly.
        } finally {

            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null == base64) {
                mTask.handleEncodeState(STATE_FAILED);
                Log.e(TAG, "Thread " + sequence + ": There is no base64 string");

            } else {

                // return result to UI thread
                final String finalBase64 = base64;
                Base64Image.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mTask.onEncoded(finalBase64);
                    }
                });

                // Reports a status of "completed"
                mTask.handleEncodeState(STATE_COMPLETED);
            }

            // Sets the current Thread to null, releasing its storage
            mTask.setEncodeThread(null);

            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }
    }

    public interface TaskRunnableEncode {
        void setEncodeThread(Thread currentThread);

        void onEncoded(String base64);

        void handleEncodeState(int state);
    }
}