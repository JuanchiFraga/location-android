package com.rembertime.app.permission.gps

import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*

class TurnGpsAlert(
    private val activity: Activity,
    private val gpsListener: GpsListener
) : LocationListener, ResultCallback<LocationSettingsResult> {

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest

    init {
        buildGoogleApiClient()
    }

    /**
     * Utility method to check the location settings.
     */
    fun checkLocationSettings() {
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest)
        result.setResultCallback(this)
    }

    override fun onResult(result: LocationSettingsResult) {
        val status = result.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> gpsListener.onGpsTurnOn()
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
            } catch (e: SendIntentException) {
                //Nothing to do
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
            }
            else -> {
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        disconnect()
    }

    private fun buildGoogleApiClient() = synchronized(this) {
        googleApiClient = GoogleApiClient.Builder(activity.applicationContext).addApi(LocationServices.API).build()
        googleApiClient.connect()
        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        buildLocationSettingsRequest()
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build()
    }

    private fun removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
    }

    private fun disconnect() {
        if (!googleApiClient.isConnected) {
            return
        }
        removeLocationUpdates()
        googleApiClient.disconnect()
    }

    companion object {

        const val REQUEST_CHECK_SETTINGS = 1

        fun display(activity: Activity, gpsListener: GpsListener) {
            val turnGpsAlert = TurnGpsAlert(activity, gpsListener)
            turnGpsAlert.checkLocationSettings()
        }
    }
}
