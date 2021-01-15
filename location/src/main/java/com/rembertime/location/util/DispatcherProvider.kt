package com.rembertime.location.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class DispatcherProvider(
    val ui: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher
) {
    constructor() : this(Dispatchers.Main, Dispatchers.IO, Dispatchers.Default)
}