package com.rembertime.app.permission.gps

import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest

class TurnGpsAlert(
    private val activity: Activity,
    private val gpsListener: GpsListener
) : LocationListener, ResultCallback<LocationSettingsResult> {

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationSettingsRequest: LocationSettingsRequest

    init {
        buildGoogleApiClient()
    }

    override fun onResult(result: LocationSettingsResult) = when (result.status.statusCode) {
        LocationSettingsStatusCodes.SUCCESS -> gpsListener.onGpsTurnOn()
        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> startResolutionForResult(result.status)
        else -> { }
    }

    private fun startResolutionForResult(status: Status) {
        try {
            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
        } catch (e: SendIntentException) {
            /*  Nothing to do */
        }
    }

    override fun onLocationChanged(location: Location) {
        disconnect()
    }

    private fun checkLocationSettings() {
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest)
        result.setResultCallback(this)
    }

    private fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(activity.applicationContext).addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
    }

    private fun disconnect() {
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient.disconnect()
        }
    }

    companion object {

        const val REQUEST_CHECK_SETTINGS = 1

        fun display(activity: Activity, gpsListener: GpsListener) {
            val turnGpsAlert = TurnGpsAlert(activity, gpsListener)
            turnGpsAlert.checkLocationSettings()
        }
    }
}