package com.rembertime.location.source.listener

import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.rembertime.location.util.DispatcherProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface GoogleConnectionListener : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    var dispatcherProvider: DispatcherProvider

    override fun onConnected(bundle: Bundle?) {
        GlobalScope.launch(dispatcherProvider.io) {
            onLocationConnected(bundle)
        }
    }

    override fun onConnectionFailed(conectionResult: ConnectionResult) {
        GlobalScope.launch(dispatcherProvider.io) {
            onLocationConnectionFailed(conectionResult)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        GlobalScope.launch(dispatcherProvider.io) {
            onLocationConnectionSuspended(i)
        }
    }

    override fun onLocationChanged(location: Location?) {
        GlobalScope.launch(dispatcherProvider.io) {
            onLocationConnectionChanged(location)
        }
    }

    suspend fun onLocationConnected(bundle: Bundle?)

    suspend fun onLocationConnectionFailed(connectionResult: ConnectionResult)

    suspend fun onLocationConnectionSuspended(i: Int)

    suspend fun onLocationConnectionChanged(location: Location?)
}