package com.rembertime.app.ui.activity

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.rembertime.app.R
import com.rembertime.app.permission.activity.LocationPermissionActivity
import com.rembertime.app.ui.model.LocationSampleUiModel
import com.rembertime.app.ui.viewmodel.LocationSampleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationSampleActivity : AppCompatActivity() {

    private val viewModel: LocationSampleViewModel by viewModels()
    private val listenUpdates = Observer { locationModel: LocationSampleUiModel ->
        locationModel.showUserLocation.consume()?.let { showUserLocation(it) }
        locationModel.showError.consume()?.let { showError(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.locationSampleModel.observe(this, listenUpdates)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun showUserLocation(userLocation: Location) {
       Toast.makeText(this, "USER LOCATION: $userLocation", Toast.LENGTH_LONG).show()
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun checkPermission() {
        if (viewModel.havePermissionAndGpsIsEnabled()) {
            viewModel.fetchUserLocation()
        } else {
            startActivityForResult(LocationPermissionActivity.newIntent(this), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 12289
    }
}