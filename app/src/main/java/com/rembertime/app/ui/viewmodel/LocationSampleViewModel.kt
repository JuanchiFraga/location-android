package com.rembertime.app.ui.viewmodel

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rembertime.app.ui.model.LocationSampleUiModel
import com.rembertime.app.util.Event
import com.rembertime.location.usecase.GetLocationUseCase
import com.rembertime.location.util.DispatcherProvider
import com.rembertime.location.util.LocationUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationSampleViewModel @ViewModelInject constructor(
    private val locationUtils: LocationUtils,
    private val getLocation: GetLocationUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private var _locationSampleModel = MutableLiveData<LocationSampleUiModel>()
    val locationSampleModel: LiveData<LocationSampleUiModel>
        get() = _locationSampleModel

    fun fetchUserLocation() =  viewModelScope.launch(dispatcherProvider.io) {
        getLocation()?.let {
            updateUi(userLocation = it)
        } ?: run {
            updateUi(errorMessage = "Couldn't find any location")
        }
    }

    private suspend fun updateUi(
        userLocation: Location? = null,
        errorMessage: String? = null
    ) = withContext(dispatcherProvider.ui)  {
        _locationSampleModel.value = LocationSampleUiModel(
            showUserLocation = Event(userLocation),
            showError = Event(errorMessage)
        )
    }

    fun havePermissionAndGpsIsEnabled(): Boolean = locationUtils.havePermission() && locationUtils.isGpsEnabled()
}