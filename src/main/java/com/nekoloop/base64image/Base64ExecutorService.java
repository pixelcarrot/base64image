package com.nekoloop.base64image;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/29/16.
 */
public class Base64ExecutorService extends ThreadPoolExecutor {
    private static final int DEFAULT_THREAD_COUNT = 3;

    Base64ExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new Utils.Base64ThreadFactory());
    }
}
