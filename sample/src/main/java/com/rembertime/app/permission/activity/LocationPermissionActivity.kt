package com.rembertime.app.permission.activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.rembertime.app.R
import com.rembertime.app.permission.gps.GpsListener
import com.rembertime.app.permission.gps.TurnGpsAlert
import com.rembertime.app.permission.model.LocationPermissionUiModel
import com.rembertime.app.permission.viewmodel.LocationPermissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.location_permission_activity.*

@AndroidEntryPoint
class LocationPermissionActivity : AppCompatActivity(), GpsListener {

    private val viewModel: LocationPermissionViewModel by viewModels()
    private val listenUpdates = Observer { permissionModel: LocationPermissionUiModel ->
        permissionModel.showGpsAlert.consume()?.let { showGpsAlert() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_permission_activity)
        checkPermission()
        viewModel.permissionModel.observe(this, listenUpdates)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.havePermissionAndGpsIsEnabled()) {
            onPermissionGranted()
        }
    }

    override fun onGpsTurnOn() {
        onPermissionGranted()
    }

    private fun showGpsAlert() {
        TurnGpsAlert.display(this, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && viewModel.havePermissions()) {
            viewModel.onPermissionGranted()
        }
        if (requestCode == REQUEST_TURN_ON_GPS && resultCode == GPS_ENABLED && viewModel.havePermissionAndGpsIsEnabled()) {
            onGpsTurnOn()
        }
    }

    private fun checkPermission() {
        if (viewModel.havePermissionAndGpsIsEnabled()) {
            onPermissionGranted()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (arePermissionsGranted(requestCode, grantResults)) {
            viewModel.onPermissionGranted()
        }
        setPermissionButtonListener()
    }

    private fun setPermissionButtonListener() {
        if (hasAnyPermissionPermanentlyDenied()) {
            permissionButton.setOnClickListener { openSettings() }
        } else {
            permissionButton.setOnClickListener { requestPermissions() }
        }
        if (viewModel.havePermissions() && viewModel.isGpsEnabled().not()) {
            permissionButton.setOnClickListener { showGpsAlert() }
        }
    }

    private fun arePermissionsGranted(requestCode: Int, grantResults: IntArray): Boolean {
        return viewModel.havePermissionAndGpsIsEnabled()
               || requestCode == REQUEST_CODE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS.toTypedArray(), REQUEST_CODE_PERMISSION)
    }

    private fun openSettings() = Intent().apply {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun hasAnyPermissionPermanentlyDenied(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LOCATION_PERMISSIONS.any { permission -> shouldShowRequestPermissionRationale(permission).not() }
        } else {
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onPermissionRejected()
    }

    private fun onPermissionGranted() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun onPermissionRejected() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 12
        private const val GPS_ENABLED = -1
        const val REQUEST_TURN_ON_GPS = 5432
        private const val SETTINGS_REQUEST_CODE = 102
        val LOCATION_PERMISSIONS = listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

        fun newIntent(context: Context) = Intent(context, LocationPermissionActivity::class.java)
    }
}