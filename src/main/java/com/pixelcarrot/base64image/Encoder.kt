package com.pixelcarrot.base64image

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException

internal class Encoder(
    private val bitmap: Bitmap,
    private val listener: Listener<String?>
) : Runnable {

    companion object {
        private const val NUMBER_OF_TRIES = 3
        private const val SLEEP_TIME: Long = 500
    }

    override fun run() {
        var base64: String? = null
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()

        try {
            listener.onStart()
            if (Thread.interrupted()) {
                return
            }
            for (i in 0 until NUMBER_OF_TRIES) {
                if (i != 0) {
                    listener.onRetry(i)
                }
                try {
                    Thread.sleep(SLEEP_TIME)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    return
                }
                try {
                    base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                    break
                } catch (e: Throwable) {
                    System.gc()
                    if (Thread.interrupted()) {
                        return
                    }
                    try {
                        Thread.sleep(SLEEP_TIME)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        return
                    }
                }
            }
        } finally {
            try {
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Handler(Looper.getMainLooper()).post {
                listener.onCompleted(base64)
            }
            Thread.interrupted()
        }

    }

}