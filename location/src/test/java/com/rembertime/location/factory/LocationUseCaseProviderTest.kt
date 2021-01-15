package com.rembertime.location.factory

import com.nhaarman.mockitokotlin2.mock
import com.rembertime.location.strategy.PriorityType
import com.rembertime.location.strategy.RetryType
import junit.framework.TestCase.assertNotNull
import org.junit.Test

class LocationUseCaseProviderTest {

    @Test(expected = IllegalStateException::class)
    fun givenNegativeAttemptThenThrowIllegalStateException() {
        LocationUseCaseProvider.Builder(mock()).withAttempts(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun givenNegativeRetryThenThrowIllegalStateException() {
        LocationUseCaseProvider.Builder(mock()).withRetryDelayInMillis(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun givenNegativeTimeOutPerAttemptThenThrowIllegalStateException() {
        LocationUseCaseProvider.Builder(mock()).withTimeOutPerAttemptInMillis(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun givenNegativeRequestNumberThenThrowIllegalStateException() {
        LocationUseCaseProvider.Builder(mock()).withNumberOfLocationUpdates(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun givenNegativeIntervalReceivingInMillisThenThrowIllegalStateException() {
        LocationUseCaseProvider.Builder(mock()).withIntervalReceivingInMillis(-1)
    }

    @Test(expected = IllegalStateException::class)
    fun givenIntervalReceivingLessThanFastestThenThrowIllegalStateException() {
        LocationUseCaseProvider.Builder(mock())
            .withFastestIntervalReceivingInMillis(10)
            .withIntervalReceivingInMillis(5)
    }

    @Test(expected = IllegalStateException::class)
    fun givenNegativeFastestIntervalReceivingInMillisThenThrowIllegalStateException() {
        LocationUseCaseProvider.Builder(mock()).withFastestIntervalReceivingInMillis(-1)
    }

    @Test
    fun givenCorrectLocationUseCaseIsCreatedThenProvideAnInstanceOfIt() {
        val locationUseCase = LocationUseCaseProvider.Builder(mock())
            .withTimeOutPerAttemptInMillis(200)
            .withRetryDelayInMillis(150)
            .withAttempts(5)
            .withRetryStrategy(RetryType.EXPONENTIAL_BACK_OFF)
            .withNumberOfLocationUpdates(1)
            .withFastestIntervalReceivingInMillis(500)
            .withIntervalReceivingInMillis(1000)
            .withRequestPriority(PriorityType.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        assertNotNull(locationUseCase)
    }
}