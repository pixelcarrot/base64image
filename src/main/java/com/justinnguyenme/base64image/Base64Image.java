package com.justinnguyenme.base64image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/16/16.
 */
public class Base64Image {

    static final Handler HANDLER = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    throw new AssertionError("Unknown handler message received: " + msg.what);
            }
        }
    };

    private static volatile Base64Image singleton = null;
    final ExecutorService service;
    private final Context context;

    private Base64Image(Context context, ExecutorService service) {
        this.context = context;
        this.service = service;
    }

    public static Base64Image with(Context context) {
        if (singleton == null) {
            synchronized (Base64Image.class) {
                if (singleton == null) {

                    float scaleFactor = 0.5f;
                    int cpuCount = Runtime.getRuntime().availableProcessors();
                    int maxThreads = (int) (cpuCount * scaleFactor);
                    maxThreads = (maxThreads > 0 ? maxThreads : 1);

                    final ExecutorService service = new ThreadPoolExecutor(
                            maxThreads, // core thread pool size
                            maxThreads, // maximum thread pool size
                            60, TimeUnit.SECONDS,
                            new LinkedBlockingDeque<Runnable>(),
                            new ThreadPoolExecutor.CallerRunsPolicy());

                    singleton = new Base64Image(context, service);
                }
            }
        }
        return singleton;
    }

    public RequestDecode decode(String base64) {
        if (base64.trim().length() == 0) {
            throw new IllegalArgumentException("Base64 must not be empty.");
        }
        return new RequestDecode(this, base64);
    }

    public RequestEncode encode(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap must not be empty.");
        }
        return new RequestEncode(this, bitmap);
    }
}
