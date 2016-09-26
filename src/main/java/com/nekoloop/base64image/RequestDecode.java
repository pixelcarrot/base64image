package com.nekoloop.base64image;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/19/16.
 */
public class RequestDecode {

    private static final String TAG = RequestDecode.class.getSimpleName();

    private final Base64Image manager;
    private final String path;
    private final Provider provider;
    private final RequestCache cache;

    RequestDecode(Base64Image manager, String path, Provider provider) {
        this.manager = manager;
        this.path = path;
        this.provider = provider;
        this.cache = new RequestCache(manager, path);
    }

    public void into(final Target callback) {

        File file = cache.getFile();

        if (file.exists()) {
            callback.onSuccess(file);
        } else {

            provider.setListener(path, new ValueListener() {
                @Override
                public void onBase64Loaded(String base64) {

                    asyncDecode(base64, new Decode() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {

                            cache.asyncSaveToFile(bitmap, new RequestCache.FileLoader() {
                                @Override
                                public void onSuccess(File file) {
                                    callback.onSuccess(file);
                                }

                                @Override
                                public void onFail() {
                                    callback.onFail();
                                }
                            });
                        }

                        @Override
                        public void onFail() {
                            callback.onFail();
                        }
                    });
                }

                @Override
                public void onBase64Failed() {
                    callback.onFail();
                }
            });
        }
    }

    private void asyncDecode(String base64, final Decode callback) {

        manager.service.submit(new BitmapDecode(base64, new BitmapDecode.TaskRunnableDecode() {
            @Override
            public void setDecodeThread(Thread currentThread) {
                Log.d(TAG, currentThread.toString() + " " + path);
            }

            @Override
            public void onDecoded(Bitmap bitmap) {
                callback.onSuccess(bitmap);
            }

            @Override
            public void handleDecodeState(int state) {
                if (state == BitmapDecode.STATE_FAILED) {
                    callback.onFail();
                }
            }
        }));
    }

    public interface Target {
        void onSuccess(File file);

        void onFail();
    }

    private interface Decode {
        void onSuccess(Bitmap bitmap);

        void onFail();
    }

    public interface Provider {
        void setListener(String path, ValueListener listener);
    }

    public interface ValueListener {
        void onBase64Loaded(String base64);

        void onBase64Failed();
    }
}
