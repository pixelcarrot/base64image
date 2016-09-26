package com.nekoloop.base64image;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/19/16.
 */
public class RequestEncode {

    private static final String TAG = RequestEncode.class.getSimpleName();

    private final Base64Image manager;
    private final Bitmap bitmap;

    RequestEncode(Base64Image manager, Bitmap bitmap) {
        this.manager = manager;
        this.bitmap = bitmap;
    }

    public void into(final Encode callback) {
        asyncEncode(bitmap, callback);
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
                if (state == BitmapEncode.STATE_FAILED) {
                    callback.onFailure();
                }
            }
        }));
    }

    public interface Encode {
        void onSuccess(String base64);

        void onFailure();
    }
}
