package com.rembertime.location.strategy

import junit.framework.TestCase.assertEquals
import com.rembertime.location.strategy.RetryType.LINEAL
import com.rembertime.location.strategy.RetryType.EXPONENTIAL_BACK_OFF
import org.junit.Test
import kotlin.math.pow

class RetryStrategyTest {

    private val linealStrategy = LINEAL
    private val backoffStrategy = EXPONENTIAL_BACK_OFF

    @Test
    fun givenLinealStrategyWithOneAttemptThenDoNotApplyChangesOnDelay() {
        val initialRetryDelayInMillis = 100L
        val attempts = 1

        assertEquals(initialRetryDelayInMillis, linealStrategy.getDelay(initialRetryDelayInMillis, attempts))
    }

    @Test
    fun givenLinealStrategyWithManyAttemptsThenDoNotApplyChangesOnDelay() {
        val initialRetryDelayInMillis = 100L
        val attempts = 10

        assertEquals(initialRetryDelayInMillis, linealStrategy.getDelay(initialRetryDelayInMillis, attempts))
    }

    @Test
    fun givenExponentialBackoffStrategyWithOneAttemptThenDoNotApplyChangesOnDelay() {
        val initialRetryDelayInMillis = 100L
        val attempts = 1

        assertEquals(initialRetryDelayInMillis, backoffStrategy.getDelay(initialRetryDelayInMillis, attempts))
    }

    @Test
    fun givenExponentialBackoffStrategyWithManyAttemptsThenIncreaseDelayPerAttemptAsInitialDelayProductTwoRaisedToAttempt() {
        val initialRetryDelayInMillis = 100L
        val attempts = 10

        val delayResult = (initialRetryDelayInMillis * 2.toDouble().pow(attempts - 1)).toLong()

        assertEquals(delayResult, backoffStrategy.getDelay(initialRetryDelayInMillis, attempts))
    }
}