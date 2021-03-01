package com.pixelcarrot.base64image

internal abstract class SimpleListener<T> : Listener<T> {
    override fun onStart() {
    }

    override fun onRetry(count: Int) {
    }

    override fun onCompleted(result: T) {
    }
}