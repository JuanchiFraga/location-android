package com.rembertime.app.model

import com.rembertime.app.util.Event

data class LocationSampleUiModel(
    val showLoading: Boolean,
    val showUserLocation: Event<List<RowModel>?>,
    val showError: Event<Int?>
)