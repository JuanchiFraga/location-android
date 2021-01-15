package com.rembertime.location.source

import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.any
import com.rembertime.location.factory.LocationUseCaseProvider
import com.rembertime.location.strategy.PriorityType
import com.rembertime.location.strategy.RetryType
import com.rembertime.location.util.LocationUtils
import com.rembertime.location.util.mockDispatcherProvider
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.invocation.InvocationOnMock

@ExperimentalCoroutinesApi
class GoogleLocationSourceTest {

    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var locationUtils: LocationUtils
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleLocationProvider: GoogleLocationSource
    private lateinit var properties: LocationUseCaseProvider.Builder

    @Before
    fun setUp() {
        locationProviderClient = mock()
        locationUtils = mock()
        googleApiClient = mock()
        properties = mockProperties()
        googleLocationProvider = GoogleLocationSource(locationProviderClient, mockDispatcherProvider(), locationUtils, properties).apply {
            googleApiClient = this@GoogleLocationSourceTest.googleApiClient
        }
    }

    @Test
    fun givenLocationEnabledAndGoogleClientIsNotConnectedWhenGetThenConnects() = runBlockingTest {
        mockConnectionStatus(isEnabled = true, isConnected = false)
        onGoogleApiClintConnected { googleLocationProvider.onLocationConnected(null) }

        googleLocationProvider.get()

        verify(googleApiClient).connect()
    }

    @Test
    fun givenLocationNotEnabledAndGoogleClientIsNotConnectedWhenGetThenDoesNotConnect() = runBlockingTest {
        mockConnectionStatus(isEnabled = false, isConnected = false)

        googleLocationProvider.get()

        verify(googleApiClient, never()).connect()
    }

    @Test
    fun givenLocationNotEnabledAndGoogleClientIsConnectedWhenGetThenDoesNotConnect() = runBlockingTest {
        mockConnectionStatus(isEnabled = false, isConnected = true)

        googleLocationProvider.get()

        verify(googleApiClient, never()).connect()
    }

    @Test
    fun givenLocationNotEnabledThenReturnNull() = runBlockingTest {
        whenever(locationUtils.isGpsEnabled()).thenReturn(false)

        val actualLocation = googleLocationProvider.get()

        assertNull(actualLocation)
    }

    @Test
    fun givenLocationNotEnabledThenDoesNotRequestLocation() = runBlockingTest {
        whenever(locationUtils.isGpsEnabled()).thenReturn(false)

        googleLocationProvider.get()

        verify(locationProviderClient, never()).lastLocation
    }

    @Test
    fun givenLocationEnabledWhenLocationIsDisabledAfterGetThenRequestsLocation() = runBlockingTest {
        mockConnectionStatus(isEnabled = true, isConnected = false)
        onGoogleApiClintConnected { googleLocationProvider.onLocationConnected(null) }

        googleLocationProvider.get()

        verify(locationProviderClient).lastLocation
    }

    @Test
    fun givenLocationEnabledAndGoogleClientIsConnectedThenRequestsLocation() = runBlockingTest {
        mockOnConnectedEvent()
        givenLocationSuccess(null)

        googleLocationProvider.get()

        verify(locationProviderClient).lastLocation
    }

    @Test
    fun givenAttemptsAreConsumedAndFetchAnyLocationThenRetrieveNull() = runBlockingTest {
        mockOnConnectedEvent()
        givenLocationSuccess(null)

        val actualLocation = googleLocationProvider.get()

        assertNull(actualLocation)
    }

    @Test
    fun givenLocationFailureThenExpectedLocationRequestException() = runBlockingTest {
        mockOnConnectedEvent()
        givenLocationFailure()

        val actualLocation = googleLocationProvider.get()

        assertNull(actualLocation)
    }

    @Test
    fun givenLocationEnabledButGoogleClientIsSuspendedThenReturnNullLocation() = runBlockingTest {
        mockConnectionStatus(isEnabled = true, isConnected = false)
        onGoogleApiClintConnected { googleLocationProvider.onLocationConnectionSuspended(0) }

        val actualLocation = googleLocationProvider.get()

        assertNull(actualLocation)
    }

    @Test
    fun givenLocationEnabledButGoogleClientIsHaveFailureThenReturnNull() = runBlockingTest {
        mockConnectionStatus(isEnabled = true, isConnected = false)
        onGoogleApiClintConnected { googleLocationProvider.onLocationConnectionFailed(mock()) }

        val actualLocation = googleLocationProvider.get()

        assertNull(actualLocation)
    }

    @Test
    fun givenLocationChangedAndGoogleApiClientIsDisconnectedThenDoesNotDisconnect() = runBlockingTest {
        mockConnectionStatus(isEnabled = true, isConnected = false)
        onGoogleApiClintConnected { googleLocationProvider.onLocationConnectionFailed(mock()) }
        googleLocationProvider.onLocationConnectionChanged(mock())

        googleLocationProvider.get()

        assertIsDisconnected(false)
    }

    @Test
    fun givenLocationChangedAndGoogleApiClientIsConnectedThenDisconnect() = runBlockingTest {
        mockConnectionStatus(isEnabled = true, isConnected = true)
        givenLocationFailure()
        googleLocationProvider.onLocationConnectionChanged(mock())

        googleLocationProvider.get()

        assertIsDisconnected(true)
    }

    @Suppress("UNCHECKED_CAST")
    private fun givenLocationSuccess(location: Location?) {
        val task: Task<Location> = mock()
        whenever(locationProviderClient.lastLocation).thenReturn(task)
        doAnswer { invocation: InvocationOnMock ->
            val listener = invocation.arguments[0] as OnSuccessListener<Location?>
            listener.onSuccess(location)
            null
        }.whenever(task).addOnSuccessListener(any())
    }

    private fun givenLocationFailure() {
        val task: Task<Location> = mock()
        whenever(locationProviderClient.lastLocation).thenReturn(task)
        doAnswer { invocation: InvocationOnMock ->
            val listener = invocation.arguments[0] as OnFailureListener
            listener.onFailure(RuntimeException())
            null
        }.whenever(task).addOnFailureListener(any())
    }

    private fun onGoogleApiClintConnected(isConnected: Boolean = true, resultFunc: suspend () -> Unit) {
        doAnswer {
            whenever(googleApiClient.isConnected).thenReturn(isConnected)
            givenLocationSuccess(null)
            runBlockingTest { resultFunc() }
            null
        }.whenever(googleApiClient).connect()
    }

    private fun assertIsDisconnected(isConnected: Boolean) {
        val mode = if (isConnected) times(1) else never()
        verify(googleApiClient, mode).unregisterConnectionFailedListener(googleLocationProvider)
        verify(googleApiClient, mode).unregisterConnectionCallbacks(googleLocationProvider)
        verify(googleApiClient, mode).disconnect()
    }

    private fun mockConnectionStatus(isEnabled: Boolean, isConnected: Boolean) {
        whenever(locationUtils.isGpsEnabled()).thenReturn(isEnabled)
        whenever(locationUtils.havePermission()).thenReturn(isEnabled)
        whenever(googleApiClient.isConnected).thenReturn(isConnected)
    }

    private fun mockOnConnectedEvent() {
        mockConnectionStatus(isEnabled = true, isConnected = true)
        googleLocationProvider.onConnected(null)
    }

    private fun mockProperties(): LocationUseCaseProvider.Builder {
        val mockProperties = mock<LocationUseCaseProvider.Builder>()
        whenever(mockProperties.requestNumber).thenReturn(1)
        whenever(mockProperties.priority).thenReturn(PriorityType.PRIORITY_BALANCED_POWER_ACCURACY)
        whenever(mockProperties.retryStrategy).thenReturn(RetryType.LINEAL)
        return mockProperties
    }
}