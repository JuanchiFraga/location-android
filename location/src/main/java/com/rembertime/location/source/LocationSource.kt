package com.rembertime.location.source

import android.location.Location

interface LocationSource {

    /**
     * Retrieve the current user location or null if the app has no location permissions
     * or the location couldn't be retrieved
     *
     * @return The current user location or null
     */
    suspend fun get(): Location?
}