package com.nekoloop.base64image;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/18/16.
 */
public class FileRunnable implements Runnable {

    // Constants for indicating the state of the encode
    static final int STATE_FAILED = -1;
    static final int STATE_STARTED = 0;
    static final int STATE_COMPLETED = 1;
    private static final String TAG = FileRunnable.class.getSimpleName();
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();
    // Limits the number of times the encoder tries to process an image
    private static final int NUMBER_OF_TRIES = 2;
    // Tells the Runnable to pause for a certain number of milliseconds
    private static final long SLEEP_TIME_MILLISECONDS = 250;
    final int sequence;
    final Writer mTask;
    final Bitmap mBitmap;
    final File mFile;

    public FileRunnable(File file, Bitmap bitmap, Writer task) {
        sequence = SEQUENCE_GENERATOR.incrementAndGet();
        mFile = file;
        mBitmap = bitmap;
        mTask = task;
    }

    @Override
    public void run() {

        mTask.setThread(Thread.currentThread());

        try {
            mTask.handleState(STATE_STARTED);

            if (Thread.interrupted()) {
                return;
            }

            for (int i = 0; i < NUMBER_OF_TRIES; i++) {

                try {
                    // Create a file at the file path, and open it for writing obtaining the output stream
                    mFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(mFile);
                    // Write the bitmap to the output stream (and thus the file) in PNG format (lossless compression)
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    // Flush and close the output stream
                    fos.flush();
                    fos.close();

                } catch (Throwable e) {
                    // Log anything that might go wrong with IO to file
                    Log.e("", "Error when saving image to cache.", e);

                    if (Thread.interrupted()) {
                        return;
                    }

                    try {
                        Thread.sleep(SLEEP_TIME_MILLISECONDS);
                    } catch (java.lang.InterruptedException interruptException) {
                        return;
                    }
                }
            }
        } finally {

            if (null == mFile || mFile.exists() == false) {

                mTask.handleState(STATE_FAILED);

                Log.e(TAG, "Thread " + sequence + ": There is no file");

            } else {

                // return result to UI thread
                Base64Image.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mTask.onSuccess(mFile);
                    }
                });

                mTask.handleState(STATE_COMPLETED);
            }

            mTask.setThread(null);

            Thread.interrupted();

        }
    }

    public interface Writer {
        void setThread(Thread currentThread);

        void onSuccess(File file);

        void handleState(int state);
    }
}
