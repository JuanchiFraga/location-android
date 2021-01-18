package com.rembertime.location.source

import android.location.Location
import android.os.Bundle
import android.os.Looper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.rembertime.location.factory.LocationUseCaseProvider
import com.rembertime.location.source.listener.GoogleConnectionListener
import com.rembertime.location.util.DispatcherProvider
import com.rembertime.location.util.LocationUtils
import com.rembertime.location.util.extension.await
import kotlinx.coroutines.CompletableDeferred
import java.lang.RuntimeException

class GoogleLocationSource(
    private val locationProviderClient: FusedLocationProviderClient,
    override var dispatcherProvider: DispatcherProvider,
    private val locationUtils: LocationUtils,
    private val properties: LocationUseCaseProvider.Builder
) : GoogleConnectionListener, LocationSource {

    private val googleApiClientConnectedStatus = CompletableDeferred<Boolean?>()
    private var locationCompletableDeferred = CompletableDeferred<Location?>()
    lateinit var googleApiClient: GoogleApiClient

    private val locationRequest = LocationRequest().apply {
        properties.requestNumber?.let { this.setNumUpdates(it) }
        this.interval = properties.intervalReceivingInMillis
        this.fastestInterval = properties.fastestIntervalReceivingInMillis
        this.priority = properties.priority.value
    }

    override suspend fun get(): Location? {
        return if (havePermissionAndGpsIsEnabled()) fetchCurrentDeviceLocation() else null
    }

    private suspend fun fetchCurrentDeviceLocation(): Location? {
        updateLocationIfNecessary()
        waitForGoogleApiClientConnection()?.let { requestLocation() }
        return locationCompletableDeferred.await(properties.timeOutInMillis)
    }

    private fun havePermissionAndGpsIsEnabled(): Boolean {
        return locationUtils.isGpsEnabled() && locationUtils.havePermission()
    }

    private fun updateLocationIfNecessary() {
        if (googleApiClient.isConnected) updateLocation() else googleApiClient.connect()
    }

    @SuppressWarnings("MissingPermission", "detekt:TooGenericExceptionCaught")
    private fun updateLocation() {
        try { Looper.prepare() } catch (e: RuntimeException) { /* happens if the looper has already been prepared  */ }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this, Looper.myLooper())
    }

    private suspend fun waitForGoogleApiClientConnection(): Boolean? {
        return googleApiClientConnectedStatus.await()
    }

    @SuppressWarnings("MissingPermission")
    private fun requestLocation() {
        if (locationUtils.isGpsEnabled()) {
            val lastLocationTask: Task<Location?> = locationProviderClient.lastLocation
            lastLocationTask.addOnSuccessListener { location -> onLocationSuccess(location) }
            lastLocationTask.addOnFailureListener { onLocationError() }
        }
    }

    private fun onLocationSuccess(location: Location?) {
        location?.let {
            locationCompletableDeferred.complete(it)
        } ?: run {
            onLocationError()
        }
    }

    private fun onLocationError() {
        if (locationCompletableDeferred.isActive) {
            locationCompletableDeferred.complete(null)
        }
    }

    override suspend fun onLocationConnected(bundle: Bundle?) {
        updateLocation()
        emitClientConnectedStatusIfShould()
    }

    override suspend fun onLocationConnectionFailed(connectionResult: ConnectionResult) {
        emitClientConnectedStatusIfShould()
    }

    override suspend fun onLocationConnectionSuspended(i: Int) {
        emitClientConnectedStatusIfShould()
    }

    override suspend fun onLocationConnectionChanged(location: Location?) {
        disconnect()
        emitClientConnectedStatusIfShould()
    }

    private fun emitClientConnectedStatusIfShould() {
        googleApiClientConnectedStatus.complete(googleApiClient.isConnected)
    }

    private fun disconnect() {
        if (googleApiClient.isConnected) {
            removeLocationUpdates()
            googleApiClient.unregisterConnectionCallbacks(this)
            googleApiClient.unregisterConnectionFailedListener(this)
            googleApiClient.disconnect()
        }
    }

    private fun removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
    }
}