package com.rembertime.location.usecase

import android.location.Location
import com.rembertime.location.strategy.RetryType
import com.rembertime.location.source.LocationSource
import com.rembertime.location.util.LocationUtils
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

class GetLocationUseCaseDefault(
    private val locationUtils: LocationUtils,
    private val initialRetryDelayInMillis: Long,
    private val maxAttempts: Int,
    private val retryStrategy: RetryType,
    private val locationSource: () -> LocationSource
) : GetLocationUseCase {

    private lateinit var retryCount: AtomicInteger

    override suspend operator fun invoke(): Location? {
        retryCount = AtomicInteger()
        return tryToGetUserLocation()
    }

    private suspend fun tryToGetUserLocation(): Location? {
        return locationSource().get() ?: applyRetryStrategy()
    }

    private suspend fun applyRetryStrategy(): Location? {
        return if (shouldRetry(retryCount)) onRetry(retryCount) else null
    }

    private suspend fun onRetry(retryCount: AtomicInteger): Location? {
        delay(getDelay(retryCount.get()))
        return tryToGetUserLocation()
    }

    private fun shouldRetry(retryCount: AtomicInteger): Boolean {
        return locationUtils.isGpsEnabled() && retryCount.incrementAndGet() < maxAttempts
    }

    private fun getDelay(retryCount: Int): Long {
        return retryStrategy.getDelay(initialRetryDelayInMillis, retryCount)
    }
}