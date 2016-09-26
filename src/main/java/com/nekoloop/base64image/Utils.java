package com.nekoloop.base64image;

import java.util.concurrent.ThreadFactory;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/29/16.
 */
public class Utils {

    static class Base64ThreadFactory implements ThreadFactory {
        @SuppressWarnings("NullableProblems")
        public Thread newThread(Runnable r) {
            return new Base64Thread(r);
        }
    }

    private static class Base64Thread extends Thread {
        public Base64Thread(Runnable r) {
            super(r);
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
