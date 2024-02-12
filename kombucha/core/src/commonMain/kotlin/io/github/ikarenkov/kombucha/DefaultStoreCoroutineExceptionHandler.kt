package io.github.ikarenkov.kombucha

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName

fun DefaultStoreCoroutineExceptionHandler() = CoroutineExceptionHandler { context, throwable ->
    val storeName = context[CoroutineName]
    println("Unhandled error in Coroutine store named \"$storeName\", crushing.")
    throw throwable
}