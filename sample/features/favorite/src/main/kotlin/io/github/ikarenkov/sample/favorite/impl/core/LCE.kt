@file:Suppress("detekt.ExpressionBodySyntax")

package io.github.ikarenkov.sample.favorite.impl.core

import java.io.Serializable

sealed class LCE<out T> : Serializable {

    fun <R> map(f: (T) -> R): LCE<R> {
        return when (this) {
            is Data -> Data(f(value), this.isReloading)
            is Error -> Error(this.error)
            is Loading -> Loading(this.isInitial)
            is Initial -> Initial
        }
    }

    fun <R> mapData(action: (T) -> R): R? = data?.let(action)

    val data: T?
        get() = when (this) {
            is Data -> this.value
            is Error, is Initial, is Loading -> null
        }

    val throwable: Throwable?
        get() = when (this) {
            is Error -> this.error
            else -> null
        }

    val inProgress: Boolean
        get() = this is Loading || this is Initial

    @Suppress("detekt.ClassStructure")
    object Initial : LCE<Nothing>() {
        private const val serialVersionUID: Long = -672209942124939891L
    }

    data class Data<T>(
        val value: T,
        val isReloading: Boolean = false,
    ) : LCE<T>() {
        companion object {
            private const val serialVersionUID: Long = 3393430746733121748L
        }
    }

    data class Error(val error: Throwable) : LCE<Nothing>() {
        companion object {
            private const val serialVersionUID: Long = -5345378515844049870L
        }
    }

    data class Loading<T>(val isInitial: Boolean = false, val previousValue: T? = null) : LCE<T>() {
        companion object {
            private const val serialVersionUID: Long = -2510278964850328160L
        }
    }

    companion object {
        private const val serialVersionUID: Long = -594079817251997558L
    }
}

fun <T> Result<T>.toLce() = fold(
    onSuccess = { LCE.Data(it) },
    onFailure = { LCE.Error(it) }
)
