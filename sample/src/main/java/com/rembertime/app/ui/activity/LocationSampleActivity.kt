package com.rembertime.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.rembertime.app.R
import com.rembertime.app.model.LocationSampleUiModel
import com.rembertime.app.model.RowModel
import com.rembertime.app.permission.activity.LocationPermissionActivity
import com.rembertime.app.ui.view.LocationSampleRowView
import com.rembertime.app.ui.viewmodel.LocationSampleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.location_sample_activity.*

@AndroidEntryPoint
class LocationSampleActivity : AppCompatActivity() {

    private val viewModel: LocationSampleViewModel by viewModels()
    private val listenUpdates = Observer { locationModel: LocationSampleUiModel ->
        locationModel.showUserLocation.consume()?.let { showUserLocation(it) }
        locationModel.showError.consume()?.let { showError(it) }
        showLoadingIfShould(locationModel.showLoading)
    }

    private fun showLoadingIfShould(showLoading: Boolean) {
        sampleLoading.visibility = if (showLoading) VISIBLE else GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_sample_activity)
        viewModel.locationSampleModel.observe(this, listenUpdates)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.havePermissionAndGpsIsEnabled()) {
            viewModel.fetchUserLocation()
            setButton()
        } else {
            startActivityForResult(LocationPermissionActivity.newIntent(this), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) finish()
        }
    }

    private fun setButton() {
        sampleButton.setOnClickListener {
            viewModel.fetchUserLocation()
        }
    }

    private fun showUserLocation(addressInfo: List<RowModel>) {
        sampleRowContainer.removeAllViews()
        addressInfo.forEach {
            sampleRowContainer.addView(LocationSampleRowView(this).apply {
                setTitle(it.title)
                setDescription(it.description)
            })
        }
    }

    private fun showError(errorMessage: Int) {
        Snackbar.make(sampleParent, getString(errorMessage), Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.red))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 12289
    }
}