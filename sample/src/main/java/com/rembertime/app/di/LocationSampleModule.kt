package com.rembertime.app.di

import android.content.Context
import android.location.Geocoder
import com.rembertime.location.factory.LocationUseCaseProvider
import com.rembertime.location.strategy.PriorityType.PRIORITY_BALANCED_POWER_ACCURACY
import com.rembertime.location.strategy.RetryType.EXPONENTIAL_BACK_OFF
import com.rembertime.location.usecase.GetLocationUseCase
import com.rembertime.location.util.DispatcherProvider
import com.rembertime.location.util.LocationUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocationSampleModule {

    private const val TIME_OUT_PER_ATTEMPT = 200L
    private const val RETRY_DELAY_IN_MILLIS = 150L
    private const val ATTEMPTS = 5
    private const val REQUEST_NUMBER = 1
    private const val INTERVAL = 10000L
    private const val FASTEST_INTERVAL = 5000L

    @Singleton
    @Provides
    fun provideGetLocationUseCase(@ApplicationContext context: Context): GetLocationUseCase {
        return LocationUseCaseProvider.Builder(context)
            .withTimeOutPerAttemptInMillis(TIME_OUT_PER_ATTEMPT)
            .withRetryDelayInMillis(RETRY_DELAY_IN_MILLIS)
            .withAttempts(ATTEMPTS)
            .withRetryStrategy(EXPONENTIAL_BACK_OFF)
            .withNumberOfLocationUpdates(REQUEST_NUMBER)
            .withFastestIntervalReceivingInMillis(FASTEST_INTERVAL)
            .withIntervalReceivingInMillis(INTERVAL)
            .withRequestPriority(PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
    }

    @Singleton
    @Provides
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.getDefault())
    }

    @Singleton
    @Provides
    fun provideCoroutinesDispatchers(): DispatcherProvider = DispatcherProvider()

    @Singleton
    @Provides
    fun provideLocationUtils(@ApplicationContext context: Context): LocationUtils = LocationUtils(context)
}