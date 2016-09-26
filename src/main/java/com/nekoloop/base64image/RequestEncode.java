package com.nekoloop.base64image;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/19/16.
 */
public class RequestEncode {

    private static final String TAG = RequestEncode.class.getSimpleName();

    private final Base64Image manager;
    private final String path;
    private final Provider provider;

    RequestEncode(Base64Image manager, String path, Provider provider) {
        this.manager = manager;
        this.path = path;
        this.provider = provider;
    }

    public void into(final Target callback) {

        provider.setListener(path, new ValueListener() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {

                asyncEncode(bitmap, new Encode() {
                    @Override
                    public void onSuccess(String base64) {
                        callback.onSuccess(base64);
                    }

                    @Override
                    public void onFail() {
                        callback.onFail();
                    }
                });
            }

            @Override
            public void onBitmapFailed() {
                callback.onFail();
            }
        });
    }

    private void asyncEncode(Bitmap bitmap, final Encode callback) {

        manager.service.submit(new BitmapEncode(bitmap, new BitmapEncode.TaskRunnableEncode() {
            @Override
            public void setEncodeThread(Thread currentThread) {
                Log.d(TAG, currentThread.toString());
            }

            @Override
            public void onEncoded(String base64) {
                callback.onSuccess(base64);
            }

            @Override
            public void handleEncodeState(int state) {
                Log.d(TAG, "" + state);
                if (state == BitmapEncode.STATE_FAILED) {
                    callback.onFail();
                }
            }
        }));
    }

    public interface Target {
        void onSuccess(String base64);

        void onFail();
    }

    private interface Encode {
        void onSuccess(String base64);

        void onFail();
    }

    public interface Provider {
        void setListener(String path, ValueListener listener);
    }

    public interface ValueListener {
        void onBitmapLoaded(Bitmap bitmap);

        void onBitmapFailed();
    }
}
