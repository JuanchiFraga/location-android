package com.rembertime.app.permission.model

import com.rembertime.app.util.Event

data class LocationPermissionUiModel(
    val showGpsAlert: Event<Boolean?>
)