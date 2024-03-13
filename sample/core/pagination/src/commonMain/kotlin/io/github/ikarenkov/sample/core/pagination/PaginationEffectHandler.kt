package io.github.ikarenkov.sample.core.pagination

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class PaginationEffectHandler<out T>(
    private val dataFetcher: PaginationDataFetcher<T>
) : EffectHandler<PaginationEff, PaginationMsg.Inner<T>> {

    override fun handleEff(eff: PaginationEff): Flow<PaginationMsg.Inner<T>> = flow {
        when (eff) {
            is PaginationEff.Load -> {
                val result: Result<PaginationDataFetcher.Response<T>> =
                    runCatching {
                        dataFetcher.fetch(eff.page, eff.size)
                    }
                        .onFailure { exception: Throwable ->
                            if (exception is CancellationException) {
                                throw exception
                            }
                        }
                emit(PaginationMsg.Inner.LoadResult(result, eff.page, eff.size))
            }
            is PaginationEff.CancelLoad -> TODO()
        }
    }

}

fun interface PaginationDataFetcher<out Item> {
    suspend fun fetch(page: Int, size: Int): Response<Item>

    data class Response<out Item>(
        val items: List<Item>,
        val totalPages: Int?
    )
}