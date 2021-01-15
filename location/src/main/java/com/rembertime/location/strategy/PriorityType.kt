package com.rembertime.location.strategy

@SuppressWarnings("MagicNumber")
enum class PriorityType(val value: Int) {

    /**
     * Request the most accurate locations available.
     * This will return the finest location available.
     */
    PRIORITY_HIGH_ACCURACY(100),

    /**
     * Request "block" level accuracy.
     * Block level accuracy is considered to be about 100 meter accuracy. Using a coarse accuracy such as this often consumes less power.
     */
    PRIORITY_BALANCED_POWER_ACCURACY(102),

    /**
     * Request "city" level accuracy.
     * City level accuracy is considered to be about 10km accuracy. Using a coarse accuracy such as this often consumes less power.
     */
    PRIORITY_LOW_POWER(104),

    /**
     * Request the best accuracy possible with zero additional power consumption.
     * No locations will be returned unless a different client has requested location updates
     * in which case this request will act as a passive listener to those locations.
     */
    PRIORITY_NO_POWER(105);
}