package com.nekoloop.base64image;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/19/16.
 */
public class RequestDecode {

    private static final String TAG = RequestDecode.class.getSimpleName();

    private final Base64Image manager;
    private final String base64;

    RequestDecode(Base64Image manager, String base64) {
        this.manager = manager;
        this.base64 = base64;
    }

    public void into(final Decode callback) {
        asyncDecode(base64, callback);
    }

    private void asyncDecode(String base64, final Decode callback) {

        manager.service.submit(new BitmapDecode(base64, new BitmapDecode.TaskRunnableDecode() {
            @Override
            public void setDecodeThread(Thread currentThread) {
                Log.d(TAG, currentThread.toString());
            }

            @Override
            public void onDecoded(Bitmap bitmap) {
                callback.onSuccess(bitmap);
            }

            @Override
            public void handleDecodeState(int state) {
                if (state == BitmapDecode.STATE_FAILED) {
                    callback.onFailure();
                }
            }
        }));
    }

    public interface Decode {
        void onSuccess(Bitmap bitmap);

        void onFailure();
    }
}
