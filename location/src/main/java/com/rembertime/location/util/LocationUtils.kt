package com.rembertime.location.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat

class LocationUtils(private val applicationContext: Context) {

    /**
     * Check application permissions
     *
     * @return true if have permission, false otherwise
     */
    fun havePermission(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check gps state
     *
     * @return true if gps is enabled, false otherwise
     */
    fun isGpsEnabled(): Boolean {
        val manager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
}