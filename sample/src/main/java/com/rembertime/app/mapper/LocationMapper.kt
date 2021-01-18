package com.rembertime.app.mapper

import android.location.Geocoder
import android.location.Location
import com.rembertime.app.model.RowModel
import java.util.Calendar
import java.util.Locale
import java.util.Date
import javax.inject.Inject

class LocationMapper @Inject constructor(private val geocoder: Geocoder) {

    fun toRows(location: Location, responseTime: Long) = mutableListOf<RowModel>().apply {
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0]
        add(RowModel(RESPONSE_TIME, getResponseTimeInSeconds(responseTime)))
        add(RowModel(LATITUDE, location.latitude.toString()))
        add(RowModel(LONGITUDE, location.longitude.toString()))
        add(RowModel(STREET, address.thoroughfare + " " + address.featureName))
        add(RowModel(POSTAL_CODE, address.postalCode))
        add(RowModel(CITY, address.locality))
        add(RowModel(STATE, address.adminArea))
        add(RowModel(COUNTRY, address.countryName))
    }

    private fun getResponseTimeInSeconds(responseTime: Long): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = Date(responseTime)
        return calendar.get(Calendar.SECOND).toString() + "." + calendar.get(Calendar.MILLISECOND).toString()
    }

    companion object {
        private const val RESPONSE_TIME = "Response time"
        private const val LATITUDE = "Latitude"
        private const val LONGITUDE = "Longitude"
        private const val STREET = "Street"
        private const val POSTAL_CODE = "Postal code"
        private const val CITY = "City"
        private const val STATE = "State"
        private const val COUNTRY = "Country"
    }
}