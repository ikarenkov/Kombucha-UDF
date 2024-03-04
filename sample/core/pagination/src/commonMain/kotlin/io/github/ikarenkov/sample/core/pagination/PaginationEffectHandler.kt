package io.github.ikarenkov.sample.core.pagination

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class PaginationEffectHandler<T>(
    private val dataFetcher: DataFetcher<T>
) : EffectHandler<PaginationEff, PaginationMsg.Inner> {

    override fun handleEff(eff: PaginationEff): Flow<PaginationMsg.Inner> = flow {
        when (eff) {
            is PaginationEff.Load -> emit(dataFetcher.fetch(eff))
            is PaginationEff.CancelLoad -> TODO()
        }
    }

    fun interface DataFetcher<T> {

        suspend fun fetch(input: PaginationEff.Load): PaginationMsg.Inner.LoadResult<T>

    }
}