package com.rembertime.app.repository

import android.location.Location
import com.rembertime.location.usecase.GetLocationUseCase
import javax.inject.Inject

class LocationSampleRepository @Inject constructor(private val getLocation: GetLocationUseCase) {

    private var currentLocation: Location? = null

    suspend fun getNewLocation(): Location? {
        return awaitForNewLocation()
    }

    private suspend fun awaitForNewLocation(): Location? {
        val newLocation = getLocation()
        return newLocation?.let {
            if (areTheSameLocation(newLocation)) awaitForNewLocation() else updateCurrentAndGet(newLocation)
        }
    }

    private fun areTheSameLocation(newLocation: Location): Boolean {
       return newLocation.latitude == currentLocation?.latitude && newLocation.longitude == currentLocation?.longitude
    }

    private fun updateCurrentAndGet(newLocation: Location?): Location? {
        currentLocation = newLocation
        return newLocation
    }
}