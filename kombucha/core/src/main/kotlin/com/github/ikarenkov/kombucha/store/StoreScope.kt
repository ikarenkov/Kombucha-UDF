package com.github.ikarenkov.kombucha.store

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

fun StoreScope(
    name: String? = null,
    coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }
) = CoroutineScope(
    SupervisorJob() +
            coroutineExceptionHandler +
            (name?.let { CoroutineName(name) } ?: EmptyCoroutineContext)
)