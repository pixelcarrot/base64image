package com.justinnguyenme.base64image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/16/16.
 */
public class BitmapDecode implements Runnable {

    // Constants for indicating the state of the decode
    static final int STATE_FAILED = -1;
    static final int STATE_STARTED = 0;
    static final int STATE_COMPLETED = 1;
    private static final String TAG = BitmapDecode.class.getSimpleName();
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();
    // Limits the number of times the decoder tries to process an image
    private static final int NUMBER_OF_TRIES = 3;

    // Tells the Runnable to pause for a certain number of milliseconds
    private static final long SLEEP_TIME_MILLISECONDS = 250;
    final int sequence;
    final String mBase64;
    final TaskRunnableDecode mTask;

    public BitmapDecode(String base64, TaskRunnableDecode task) {
        sequence = SEQUENCE_GENERATOR.incrementAndGet();
        mTask = task;
        mBase64 = base64;
    }

    @Override
    public void run() {

        mTask.setDecodeThread(Thread.currentThread());

        Bitmap returnBitmap = null;

        try {

            byte[] imageBuffer = Base64.decode(mBase64.getBytes(), Base64.DEFAULT);

            mTask.handleDecodeState(STATE_STARTED);

            if (Thread.interrupted()) {
                return;
            }

            for (int i = 0; i < NUMBER_OF_TRIES; i++) {
                try {

                    try {
                        Thread.sleep(SLEEP_TIME_MILLISECONDS);
                    } catch (java.lang.InterruptedException interruptException) {
                        return;
                    }

                    Log.e(TAG, "Thread " + sequence + ": Decoding");
                    returnBitmap = BitmapFactory.decodeByteArray(
                            imageBuffer,
                            0,
                            imageBuffer.length
                    );

                    break;

                } catch (Throwable e) {

                    // Logs an error
                    Log.e(TAG, "Thread " + sequence + ": Out of memory in decode stage. Throttling.");

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

            if (null == returnBitmap) {
                mTask.handleDecodeState(STATE_FAILED);
                Log.e(TAG, "Thread " + sequence + ": There is no bitmap");
            } else {

                // return result to UI thread
                final Bitmap finalReturnBitmap = returnBitmap;
                Base64Image.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mTask.onDecoded(finalReturnBitmap);
                    }
                });

                // Reports a status of "completed"
                mTask.handleDecodeState(STATE_COMPLETED);
            }

            // Sets the current Thread to null, releasing its storage
            mTask.setDecodeThread(null);

            // Clears the Thread's interrupt flag
            Thread.interrupted();

        }
    }

    public interface TaskRunnableDecode {
        void setDecodeThread(Thread currentThread);

        void onDecoded(Bitmap image);

        void handleDecodeState(int state);
    }
}