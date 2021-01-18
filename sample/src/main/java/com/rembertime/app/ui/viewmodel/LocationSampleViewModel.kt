package com.rembertime.app.ui.viewmodel

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rembertime.app.R
import com.rembertime.app.mapper.LocationMapper
import com.rembertime.app.model.LocationSampleUiModel
import com.rembertime.app.model.RowModel
import com.rembertime.app.repository.LocationSampleRepository
import com.rembertime.app.util.Event
import com.rembertime.location.usecase.GetLocationUseCase
import com.rembertime.location.util.DispatcherProvider
import com.rembertime.location.util.LocationUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationSampleViewModel @ViewModelInject constructor(
    private val locationUtils: LocationUtils,
    private val locationRepository: LocationSampleRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val mapper: LocationMapper
) : ViewModel() {

    private var _locationSampleModel = MutableLiveData<LocationSampleUiModel>()
    val locationSampleModel: LiveData<LocationSampleUiModel>
        get() = _locationSampleModel

    fun fetchUserLocation() =  viewModelScope.launch(dispatcherProvider.io) {
        updateUi(showLoading = true)
        locationRepository.getNewLocation()?.let {
            updateUi(userLocation = mapper.toRows(it), showLoading = false)
        } ?: run {
            updateUi(errorMessage = R.string.location_sample_error_message, showLoading = false)
        }
    }

    private suspend fun updateUi(
        userLocation: List<RowModel>? = null,
        @StringRes errorMessage: Int? = null,
        showLoading: Boolean = false
    ) = withContext(dispatcherProvider.ui)  {
        _locationSampleModel.value =
            LocationSampleUiModel(
                showUserLocation = Event(userLocation),
                showError = Event(errorMessage),
                showLoading = showLoading
            )
    }

    fun havePermissionAndGpsIsEnabled(): Boolean = locationUtils.havePermission() && locationUtils.isGpsEnabled()
}