package com.rembertime.app.di

import android.content.Context
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
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocationSampleModule {

    @Singleton
    @Provides
    fun provideGetLocationUseCase(@ApplicationContext context: Context): GetLocationUseCase {
        return LocationUseCaseProvider.Builder(context)
            .withTimeOutPerAttemptInMillis(200)
            .withRetryDelayInMillis(150)
            .withAttempts(5)
            .withRetryStrategy(EXPONENTIAL_BACK_OFF)
            .withNumberOfLocationUpdates(1)
            .withFastestIntervalReceivingInMillis(5000)
            .withIntervalReceivingInMillis(10000)
            .withRequestPriority(PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
    }

    @Singleton
    @Provides
    fun provideCoroutinesDispatchers(): DispatcherProvider = DispatcherProvider()

    @Singleton
    @Provides
    fun provideLocationUtils(@ApplicationContext context: Context): LocationUtils = LocationUtils(context)
}