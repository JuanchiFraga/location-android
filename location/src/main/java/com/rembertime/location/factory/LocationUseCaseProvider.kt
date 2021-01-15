package com.rembertime.location.factory

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.rembertime.location.source.GoogleLocationSource
import com.rembertime.location.source.LocationSource
import com.rembertime.location.strategy.PriorityType
import com.rembertime.location.strategy.RetryType
import com.rembertime.location.usecase.GetLocationUseCase
import com.rembertime.location.usecase.GetLocationUseCaseDefault
import com.rembertime.location.util.DispatcherProvider
import com.rembertime.location.util.LocationUtils

class LocationUseCaseProvider internal constructor(private val properties: Builder) {

    private fun create(applicationContext: Context): GetLocationUseCase {
        val locationUtils = LocationUtils(applicationContext)
        return GetLocationUseCaseDefault(locationUtils, properties.retryDelayInMillis, properties.maxAttempts, properties.retryStrategy) {
            createLocationProvider(applicationContext, DispatcherProvider(), locationUtils)
        }
    }

    private fun createLocationProvider(
        applicationContext: Context,
        dispatcherProvider: DispatcherProvider,
        locationUtils: LocationUtils
    ): LocationSource {
        val locationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        return GoogleLocationSource(locationProviderClient, dispatcherProvider, locationUtils, properties).apply {
            googleApiClient = createGoogleApiClient(applicationContext, this, this)
        }
    }

    private fun createGoogleApiClient(
        applicationContext: Context,
        onConnectionCallbacks: GoogleApiClient.ConnectionCallbacks,
        onConnectionFailedListener: GoogleApiClient.OnConnectionFailedListener
    ) = GoogleApiClient.Builder(applicationContext)
        .addConnectionCallbacks(onConnectionCallbacks)
        .addOnConnectionFailedListener(onConnectionFailedListener)
        .addApi(LocationServices.API)
        .build()

    class Builder constructor(private val applicationContext: Context) {

        var maxAttempts: Int = DEFAULT_ATTEMPTS
            private set
        var retryDelayInMillis: Long = DEFAULT_RETRY_DELAY_MILLIS
            private set
        var timeOutInMillis: Long = DEFAULT_TIME_OUT_PER_ATTEMPT_MILLIS
            private set
        var retryStrategy: RetryType = RetryType.LINEAL
            private set
        var requestNumber: Int? = null
            private set
        var intervalReceivingInMillis: Long = DEFAULT_UPDATE_INTERVAL_MILLIS
            private set
        var fastestIntervalReceivingInMillis: Long = DEFAULT_FASTEST_UPDATE_INTERVAL_MILLIS
            private set
        var priority: PriorityType = PriorityType.PRIORITY_BALANCED_POWER_ACCURACY
            private set

        /**
         * Set the request max attempts
         *
         * Default - 0. NO_ATTEMPTS by default
         *
         * @see NO_ATTEMPTS 0 retries in case of failure
         * @see INFINITE_ATTEMPTS Endlessly retries until find location
         *
         * @param maxAttempts max attempts to get the user location
         * @return The builder.
         */
        fun withAttempts(maxAttempts: Int): Builder {
            if (maxAttempts < 0) {
                throw IllegalStateException("The max attempts must be 0 or positive")
            }
            this.maxAttempts = maxAttempts
            return this
        }

        /**
         * Set max retry delay in millis
         * Waiting time between the completion of a failed attempt and the execution of the next
         *
         * Default - 300 (0.3 seg) t his value may change depending on the retry strategy you choose
         *
         * @see withRetryStrategy the retry strategy to apply
         *
         * @param retryDelayInMillis initial wait time before next request
         * @return The builder.
         */
        fun withRetryDelayInMillis(retryDelayInMillis: Long): Builder {
            if (retryDelayInMillis < 0) {
                throw IllegalStateException("The retry delay must be 0 or positive")
            }
            this.retryDelayInMillis = retryDelayInMillis
            return this
        }

        /**
         * Set the retry strategy to apply to the retryDelayInMillis
         *
         * Default - LINEAL
         *
         * @see withRetryDelayInMillis max retry delay in millis
         *
         * @param retryStrategy A {@link RetryStrategy}
         * @return The builder.
         */
        fun withRetryStrategy(retryStrategy: RetryType): Builder {
            this.retryStrategy = retryStrategy
            return this
        }

        /**
         * Set time out per attempt
         * The waiting time for the arrival of the user's location,
         * if this time is consumed without location then null is returned or a next attempt is executed
         *
         * Default - 250 (0.25 seconds)
         *
         * @param timeOutInMillis before canceling an attempt if no result found
         * @return The builder.
         */
        fun withTimeOutPerAttemptInMillis(timeOutInMillis: Long): Builder {
            if (timeOutInMillis < 0) {
                throw IllegalStateException("The timeout per attempt must be 0 or positive")
            }
            this.timeOutInMillis = timeOutInMillis
            return this
        }

        /**
         * Set the number of location updates.
         * By default locations are continuously updated until the request is explicitly removed,
         * however you can optionally request a set number of updates
         *
         * Default - is NOT set by default
         *
         * @param requestNumber number of location updates
         * @return The builder.
         */
        fun withNumberOfLocationUpdates(requestNumber: Int): Builder {
            if (requestNumber <= 0) {
                throw IllegalStateException("The number of location updates must be positive")
            }
            this.requestNumber = requestNumber
            return this
        }

        /**
         * Set the interval for receiving location updates in milliseconds.
         * If the value is 50000, a location update is received every 50 seconds.
         *
         * Default - 10000 (10 seconds)
         *
         * @param intervalReceivingInMillis interval for receiving location updates
         * @return The builder.
         */
        fun withIntervalReceivingInMillis(intervalReceivingInMillis: Long): Builder {
            if (intervalReceivingInMillis < 0 || fastestIntervalReceivingInMillis > intervalReceivingInMillis) {
                throw IllegalStateException("The interval for receiving location updates must be 0 or positive and greater than fastest")
            }
            this.intervalReceivingInMillis = intervalReceivingInMillis
            return this
        }

        /**
         * Set the fastest interval for location updates, in milliseconds.
         * This controls the fastest rate at which your application will receive location updates,
         * which might be faster than setInterval(long) in some situations
         * (for example, if other applications are triggering location updates).
         *
         * Default - 5000 (5 seconds)
         *
         * @param fastestIntervalReceivingInMillis fastest interval for location updates
         * @return The builder.
         */
        fun withFastestIntervalReceivingInMillis(fastestIntervalReceivingInMillis: Long): Builder {
            if (fastestIntervalReceivingInMillis < 0 || fastestIntervalReceivingInMillis > intervalReceivingInMillis) {
                throw IllegalStateException("The fastest interval for receiving location updates must be 0 or positive and less than interval")
            }
            this.fastestIntervalReceivingInMillis = fastestIntervalReceivingInMillis
            return this
        }

        /**
         * Set the priority of the request.
         * The priority of the request is a strong hint to the LocationClient for which location sources to use.
         *
         * Default - PRIORITY_BALANCED_POWER_ACCURACY
         *
         * @param priority A {@link PriorityType}
         * @return The builder.
         */
        fun withRequestPriority(priority: PriorityType): Builder {
            this.priority = priority
            return this
        }

        /**
         * Build location use case
         */
        fun build(): GetLocationUseCase {
            return LocationUseCaseProvider(this).create(applicationContext)
        }

        companion object {
            const val NO_ATTEMPTS = 0
            const val INFINITE_ATTEMPTS = Int.MAX_VALUE
            private const val DEFAULT_ATTEMPTS = 4
            private const val DEFAULT_RETRY_DELAY_MILLIS = 300L
            private const val DEFAULT_TIME_OUT_PER_ATTEMPT_MILLIS = 250L
            private const val DEFAULT_UPDATE_INTERVAL_MILLIS = 10000L
            private const val DEFAULT_FASTEST_UPDATE_INTERVAL_MILLIS = 5000L
        }
    }
}