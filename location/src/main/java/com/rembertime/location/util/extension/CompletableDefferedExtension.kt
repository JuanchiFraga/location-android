package com.rembertime.location.util.extension

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

suspend fun <T> CompletableDeferred<T?>.await(timeOutInMillis: Long): T? {
    val timeoutDeferred = CompletableDeferred<T?>()
    val completable = this
    GlobalScope.launch {
        delay(timeOutInMillis)
        if (timeoutDeferred.isActive) {
            timeoutDeferred.complete(null)
            completable.cancel()
        }
    }
    if (timeoutDeferred.isActive) {
        timeoutDeferred.complete(this.await())
    }
    return timeoutDeferred.await()
}