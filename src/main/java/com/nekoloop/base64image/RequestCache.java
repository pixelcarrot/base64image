package com.nekoloop.base64image;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;

/**
 * Created by Nguyen Hoang Anh Nguyen on 2/21/16.
 */
public class RequestCache {

    private static final String TAG = RequestCache.class.getSimpleName();

    private final Base64Image manager;
    private final String path;

    RequestCache(Base64Image manager, String path) {
        this.manager = manager;
        this.path = path;
    }

    public File getFile() {
        String[] splits = path.split("/");
        String key = splits[splits.length - 1];
        String folder = splits[splits.length - 2];

        File cacheDir = new File(manager.context.getCacheDir(), folder);
        if (cacheDir.exists() == false) {
            cacheDir.mkdir();
        }

        return new File(cacheDir, key);
    }

    public void asyncSaveToFile(Bitmap bitmap, final FileLoader loader) {

        File cacheFile = getFile();

        manager.service.submit(new FileRunnable(cacheFile, bitmap, new FileRunnable.Writer() {
            @Override
            public void setThread(Thread currentThread) {
                Log.d(TAG, currentThread.toString() + " " + path);
            }

            @Override
            public void onSuccess(File file) {
                loader.onSuccess(file);
            }

            @Override
            public void handleState(int state) {
                if (state == FileRunnable.STATE_FAILED) {
                    loader.onFail();
                }
            }
        }));
    }


    public interface FileLoader {
        void onSuccess(File file);

        void onFail();
    }
}
