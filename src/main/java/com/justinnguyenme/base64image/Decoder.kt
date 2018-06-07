package com.justinnguyenme.base64image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Base64


internal class Decoder(private val base64: String, private val listener: Listener<Bitmap?>) : Runnable {

    companion object {
        private const val NUMBER_OF_TRIES = 3
        private const val SLEEP_TIME: Long = 500
    }

    override fun run() {

        var bitmap: Bitmap? = null
        val imageBuffer = Base64.decode(base64.toByteArray(), Base64.DEFAULT)

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
                    bitmap = BitmapFactory.decodeByteArray(
                            imageBuffer,
                            0,
                            imageBuffer.size
                    )
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
            Handler(Looper.getMainLooper()).post {
                listener.onCompleted(bitmap)
            }
            Thread.interrupted()
        }

    }

}