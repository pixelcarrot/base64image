package com.justinnguyenme.base64image

import android.graphics.Bitmap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class Base64Image private constructor() {

    private object Holder {
        val INSTANCE = Base64Image()
    }

    companion object {
        private const val SCALE = 0.5F
        private const val KEEP_ALIVE_TIME = 60L

        val instance: Base64Image by lazy { Holder.INSTANCE }
    }

    private val service: ThreadPoolExecutor

    init {
        val cpuCount = Runtime.getRuntime().availableProcessors()
        val thread = (cpuCount * SCALE).toInt()
        val maxThreads = if (thread > 0) thread else 1

        service = ThreadPoolExecutor(
                maxThreads, // core thread pool size
                maxThreads, // maximum thread pool size
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                LinkedBlockingDeque(),
                ThreadPoolExecutor.CallerRunsPolicy())
    }

    fun encode(bitmap: Bitmap, function: (base64: String?) -> Unit) {
        val job = Encoder(bitmap, object : Listener<String?> {
            override fun onRetry(count: Int) {
            }

            override fun onStart() {
            }

            override fun onCompleted(result: String?) {
                function.invoke(result)
            }
        })

        service.submit(job)
    }

    fun decode(base64: String, function: (bitmap: Bitmap?) -> Unit) {
        val job = Decoder(base64, object : Listener<Bitmap?> {
            override fun onRetry(count: Int) {
            }

            override fun onStart() {
            }

            override fun onCompleted(result: Bitmap?) {
                function.invoke(result)
            }
        })

        service.submit(job)
    }
}