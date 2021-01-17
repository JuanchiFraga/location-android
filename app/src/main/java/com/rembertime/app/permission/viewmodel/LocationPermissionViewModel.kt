package com.rembertime.app.permission.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rembertime.app.permission.model.LocationPermissionUiModel
import com.rembertime.app.util.Event
import com.rembertime.location.util.DispatcherProvider
import com.rembertime.location.util.LocationUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationPermissionViewModel @ViewModelInject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val locationUtils: LocationUtils
) : ViewModel() {

    private var _permissionModel = MutableLiveData<LocationPermissionUiModel>()
    val permissionModel: LiveData<LocationPermissionUiModel>
        get() = _permissionModel

    fun onPermissionGranted() = viewModelScope.launch {
        updateUi(showGpsAlert = true)
    }

    fun havePermissions(): Boolean = locationUtils.havePermission()

    fun isGpsEnabled(): Boolean = locationUtils.isGpsEnabled()

    fun havePermissionAndGpsIsEnabled() = locationUtils.havePermission() && locationUtils.isGpsEnabled()

    private suspend fun updateUi(showGpsAlert: Boolean? = null) = withContext(dispatcherProvider.ui)  {
        _permissionModel.value = LocationPermissionUiModel(Event(showGpsAlert))
    }
}