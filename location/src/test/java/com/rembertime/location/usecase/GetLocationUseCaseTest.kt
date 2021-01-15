package com.rembertime.location.usecase

import android.location.Location
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.doAnswer
import com.rembertime.location.strategy.RetryType
import com.rembertime.location.source.LocationSource
import com.rembertime.location.util.LocationUtils
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetLocationUseCaseTest {

    private var initialRetryDelayInMillis = 100L
    private var maxAttempts: Int = 3
    private lateinit var locationUtils: LocationUtils
    private lateinit var retryStrategy: RetryType
    private lateinit var locationProvider: () -> LocationSource
    private lateinit var locationSource: LocationSource
    private lateinit var getLocationUseCase: GetLocationUseCase

    @Before
    fun setUp() {
        locationUtils = mock()
        retryStrategy = mock()
        locationProvider = mock()
        locationSource = mock()
        whenever(locationProvider()).thenReturn(locationSource)
        getLocationUseCase = GetLocationUseCaseDefault(locationUtils, initialRetryDelayInMillis, maxAttempts, retryStrategy, locationProvider)
    }

    @Test
    fun whenRequestUserLocationThenProvideIt() = runBlockingTest {
        val userLocation = mock<Location>()
        whenever(locationSource.get()).thenReturn(userLocation)

        val actualLocation = getLocationUseCase()

        assertEquals(userLocation, actualLocation)
    }

    @Test
    fun whenLocationIsEnabledThenReturnNull() = runBlockingTest {
        whenever(locationUtils.isGpsEnabled()).thenReturn(false)

        val actualLocation = getLocationUseCase()

        assertNull(actualLocation)
    }

    @Test
    fun whenRequestUserLocationAndDeviceHaveMuchDelayThenConsumeAttempts() = runBlockingTest {
        whenever(locationSource.get()).thenReturn(null)
        whenever(locationUtils.isGpsEnabled()).thenReturn(true)

        val actualLocation = getLocationUseCase()

        verify(locationProvider(), times(maxAttempts)).get()
        assertNull(actualLocation)
    }

    @Test
    fun whenRequestUserLocationAndDeviceHaveMuchDelayThenGetOnLastAttempt() = runBlockingTest {
        fetchLocationOnAttempt(maxAttempts - 1)
        whenever(locationUtils.isGpsEnabled()).thenReturn(true)

        val actualLocation = getLocationUseCase()

        verify(locationProvider(), times(2)).get()
        assertNotNull(actualLocation)
    }

    private suspend fun fetchLocationOnAttempt(attemptsToFetch: Int) {
        val userLocation = mock<Location>()
        var currentAttempts = 0
        doAnswer {
            currentAttempts = currentAttempts.inc()
            if (currentAttempts == attemptsToFetch) userLocation else null
        }.whenever(locationSource).get()
    }
}