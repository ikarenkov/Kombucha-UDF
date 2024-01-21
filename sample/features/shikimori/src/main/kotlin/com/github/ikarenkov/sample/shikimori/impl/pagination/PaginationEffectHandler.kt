package com.github.ikarenkov.sample.shikimori.impl.pagination

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.ikarenkov.kombucha.eff_handler.FlowEffectHandler

open class PaginationEffectHandler<T>(
    private val dataFetcher: DataFetcher<T>
) : FlowEffectHandler<PaginationFeature.Eff, PaginationFeature.Msg.Internal> {

    override fun handleEff(eff: PaginationFeature.Eff): Flow<PaginationFeature.Msg.Internal> = flow {
        when (eff) {
            is PaginationFeature.Eff.Load -> emit(dataFetcher.fetch(eff))
            is PaginationFeature.Eff.CancelLoad -> TODO()
        }
    }

    fun interface DataFetcher<T> {

        suspend fun fetch(input: PaginationFeature.Eff.Load): PaginationFeature.Msg.Internal.LoadResult<T>

    }
}