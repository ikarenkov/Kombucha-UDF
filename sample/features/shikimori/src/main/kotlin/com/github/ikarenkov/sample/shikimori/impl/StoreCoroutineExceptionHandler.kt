package com.github.ikarenkov.sample.shikimori.impl

import kotlinx.coroutines.CoroutineExceptionHandler
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal fun storeCoroutineExceptionHandler(name: String) = CoroutineExceptionHandler { _, throwable ->
    logcat(
        priority = LogPriority.ERROR,
        tag = "StoreCoroutineExceptionHandler"
    ) { "Unhandled exception in store \"$name\"" + throwable.asLog() }
}