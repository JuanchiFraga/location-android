package com.rembertime.location.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
fun mockDispatcherProvider(
    main: CoroutineDispatcher? = null,
    computation: CoroutineDispatcher? = null,
    io: CoroutineDispatcher? = null
): DispatcherProvider {
    val sharedTestCoroutineDispatcher = TestCoroutineDispatcher()
    return DispatcherProvider(
        main ?: sharedTestCoroutineDispatcher,
        io ?: sharedTestCoroutineDispatcher,
        computation ?: sharedTestCoroutineDispatcher
    )
}