@file:Suppress("detekt.ExpressionBodySyntax")

package io.github.ikarenkov.sample.favorite.impl.core

sealed class LCE<out T> {

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
    object Initial : LCE<Nothing>()

    data class Data<T>(
        val value: T,
        val isReloading: Boolean = false,
    ) : LCE<T>()

    data class Error(val error: Throwable) : LCE<Nothing>()

    data class Loading<T>(val isInitial: Boolean = false, val previousValue: T? = null) : LCE<T>()
}

fun <T> Result<T>.toLce() = fold(
    onSuccess = { LCE.Data(it) },
    onFailure = { LCE.Error(it) }
)
