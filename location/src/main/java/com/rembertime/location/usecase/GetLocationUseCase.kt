package com.rembertime.location.usecase

import android.location.Location

interface GetLocationUseCase {

    /**
     * Get the current user location or null if location is not enabled or
     * if it couldn't be retrieved after max attempts
     *
     * @return The user location or null
     */
    suspend operator fun invoke(): Location?
}