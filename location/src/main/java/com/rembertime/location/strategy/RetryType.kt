package com.rembertime.location.strategy

import kotlin.math.pow

enum class RetryType(private val delayFunction: (Long, Int) -> Long) {

    /**
     * Retries requests lineally.
     * Maintains the same waiting time between attempts.
     */
    LINEAL({ initialRetryDelayInMillis, _ -> initialRetryDelayInMillis }),

    /**
     * Retries requests exponentially.
     * Increasing the waiting time between retries up to a maximum backoff time.
     */
    EXPONENTIAL_BACK_OFF({ initialRetryDelayInMillis, retryCount ->
        (initialRetryDelayInMillis * EXPONENTIAL_RATE.pow(retryCount - 1)).toLong()
    });

    fun getDelay(initialRetryDelayInMillis: Long, retryCount: Int): Long {
        return delayFunction(initialRetryDelayInMillis, retryCount)
    }

    companion object {
        private const val EXPONENTIAL_RATE = 2.0
    }
}