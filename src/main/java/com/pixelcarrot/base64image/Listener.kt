package com.pixelcarrot.base64image

internal interface Listener<T> {

    fun onStart()

    fun onRetry(count: Int)

    fun onCompleted(result: T)

}