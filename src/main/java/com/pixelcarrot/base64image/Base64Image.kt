package com.pixelcarrot.base64image

import android.graphics.Bitmap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object Base64Image {

    private const val SCALE = 0.5F
    private const val KEEP_ALIVE_TIME = 60L

    private val cpuCount by lazy { Runtime.getRuntime().availableProcessors() }
    private val thread by lazy { (cpuCount * SCALE).toInt() }
    private val maxThreads by lazy { if (thread > 0) thread else 1 }

    private val service by lazy {
        ThreadPoolExecutor(
            maxThreads, // core thread pool size
            maxThreads, // maximum thread pool size
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            LinkedBlockingDeque(),
            ThreadPoolExecutor.CallerRunsPolicy()
        )
    }

    @JvmStatic
    fun encode(bitmap: Bitmap?, callback: (base64: String?) -> Unit) {
        val job = bitmap?.let {
            Encoder(bitmap, object : SimpleListener<String?>() {
                override fun onCompleted(result: String?) {
                    callback(result)
                }
            })
        }
        job?.let { service.submit(job) } ?: run { callback(null) }
    }

    @JvmStatic
    fun decode(base64: String?, callback: (bitmap: Bitmap?) -> Unit) {
        val job = base64?.let {
            Decoder(base64, object : SimpleListener<Bitmap?>() {
                override fun onCompleted(result: Bitmap?) {
                    callback(result)
                }
            })
        }
        job?.let { service.submit(job) } ?: run { callback(null) }
    }
}