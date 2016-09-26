package com.nekoloop.base64image;

import android.content.Context;
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

    final Context context;
    final ExecutorService service;
    final RequestDecode.Provider base64Provider;
    final RequestEncode.Provider bitmapProvider;

    private Base64Image(Context context, ExecutorService service, RequestDecode.Provider base64Provider, RequestEncode.Provider bitmapProvider) {
        this.context = context;
        this.service = service;
        this.base64Provider = base64Provider;
        this.bitmapProvider = bitmapProvider;
    }

    private static Base64Image init(Context context, ExecutorService service, RequestDecode.Provider base64Provider, RequestEncode.Provider bitmapProvider) {
        if (singleton == null) {
            synchronized (Base64Image.class) {
                if (singleton == null) {
                    singleton = new Base64Image(context, service, base64Provider, bitmapProvider);
                }
            }
        }
        return singleton;
    }

    public static Base64Image getInstance() {
        return singleton;
    }

    public RequestDecode decode(String path) {
        if (path.trim().length() == 0) {
            throw new IllegalArgumentException("Path must not be empty.");
        }
        return new RequestDecode(this, path, base64Provider);
    }

    public RequestEncode encode(String path) {
        if (path.trim().length() == 0) {
            throw new IllegalArgumentException("Path must not be empty.");
        }
        return new RequestEncode(this, path, bitmapProvider);
    }

    public static final class Builder {
        private Context context;
        private ExecutorService service;
        private RequestDecode.Provider base64Provider;
        private RequestEncode.Provider bitmapProvider;

        public Builder() {
        }

        public Base64Image build() {

            if (service == null) {

                //service = new Base64ExecutorService();

                float scaleFactor = 0.5f;
                int cpuCount = Runtime.getRuntime().availableProcessors();
                int maxThreads = (int) (cpuCount * scaleFactor);
                maxThreads = (maxThreads > 0 ? maxThreads : 1);

                service = new ThreadPoolExecutor(
                        maxThreads, // core thread pool size
                        maxThreads, // maximum thread pool size
                        60, TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>(),
                        new ThreadPoolExecutor.CallerRunsPolicy());
            }

            return Base64Image.init(context, service, base64Provider, bitmapProvider);
        }

        public Builder setContext(Context context) {
            this.context = context.getApplicationContext();
            return this;
        }

        public Builder setBase64Provider(RequestDecode.Provider provider) {
            this.base64Provider = provider;
            return this;
        }

        public Builder setBitmapProvider(RequestEncode.Provider provider) {
            this.bitmapProvider = provider;
            return this;
        }
    }
}
