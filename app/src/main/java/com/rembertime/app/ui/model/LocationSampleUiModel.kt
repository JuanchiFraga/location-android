package com.rembertime.app.ui.model

import android.location.Location
import com.rembertime.app.util.Event

data class LocationSampleUiModel(
    val showUserLocation: Event<Location?>,
    val showError: Event<String?>
)