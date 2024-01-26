package io.github.ikarenkov.sample.shikimori.impl.pagination

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.github.ikarenkov.kombucha.eff_handler.EffectHandler

open class PaginationEffectHandler<T>(
    private val dataFetcher: DataFetcher<T>
) : EffectHandler<PaginationFeature.Eff, PaginationFeature.Msg.Internal> {

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