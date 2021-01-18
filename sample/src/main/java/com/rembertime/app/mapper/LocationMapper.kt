package com.rembertime.app.mapper

import android.location.Geocoder
import android.location.Location
import com.rembertime.app.model.RowModel
import javax.inject.Inject

class LocationMapper @Inject constructor(private val geocoder: Geocoder) {

    fun toRows(location: Location) = mutableListOf<RowModel>().apply {
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0]
        add(RowModel("Country", address.countryName))
        add(RowModel("State", address.adminArea))
        add(RowModel("City", address.locality))
        add(RowModel("Street", address.thoroughfare + " " + address.featureName))
        add(RowModel("Postal code", address.postalCode))
        add(RowModel("Latitude", location.latitude.toString()))
        add(RowModel("Longitude", location.longitude.toString()))
    }
}