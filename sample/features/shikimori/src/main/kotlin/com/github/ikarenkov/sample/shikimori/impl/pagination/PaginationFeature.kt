package com.github.ikarenkov.sample.shikimori.impl.pagination

import com.github.ikarenkov.kombucha.eff_handler.adaptCast
import com.github.ikarenkov.kombucha.reducer.Reducer
import com.github.ikarenkov.kombucha.reducer.dslReducer
import com.github.ikarenkov.kombucha.store.Store
import com.github.ikarenkov.kombucha.store.StoreFactory

class PaginationFeature<T>(
    name: String,
    storeFactory: StoreFactory,
    dataFetcher: PaginationEffectHandler.DataFetcher<T>
) : Store<PaginationFeature.Msg, PaginationFeature.State<T>, PaginationFeature.Eff> by storeFactory.create(
    name = name,
    initialState = State.Initial(),
    reducer = paginationReduces<T>()::invoke,
    initialEffects = Eff.Initial(),
    PaginationEffectHandler(dataFetcher).adaptCast()
) {

    data class State<out T>(
        val items: List<T>,
        val pagesLoaded: Int,
        val nextPageLoadingState: PageLoadingState,
    ) {

        sealed interface PageLoadingState {
            data object Idle : PageLoadingState
            data object Loading : PageLoadingState
            data class Error(val throwable: Throwable) : PageLoadingState
        }

        companion object {

            fun <T> Initial() = State(
                emptyList<T>(),
                pagesLoaded = 0,
                nextPageLoadingState = PageLoadingState.Idle
            )

        }

    }

    sealed interface Msg {

//        data object Reload : Msg

        data object LoadNext : Msg
        data object RetryLoadNext : Msg

        sealed interface Internal : Msg {
            data class LoadResult<out T>(
                val result: Result<List<T>>,
                val requestedPage: Int,
                val requestedSize: Int
            ) : Internal
        }

//        data object Clear : Msg

    }

    sealed interface Eff {

        data class Load(val page: Int, val size: Int) : Eff
        data class CancelLoad(val page: Int, val size: Int) : Eff

        companion object {

            fun Initial() = setOf(Eff.Load(1, PAGE_SIZE))

        }

    }

}

const val PAGE_SIZE = 10

fun <T> paginationReduces(): Reducer<PaginationFeature.Msg, PaginationFeature.State<T>, PaginationFeature.Eff> = dslReducer { msg ->
    when (msg) {
        PaginationFeature.Msg.LoadNext -> {
            if (state.nextPageLoadingState == PaginationFeature.State.PageLoadingState.Idle) {
                state {
                    copy(nextPageLoadingState = PaginationFeature.State.PageLoadingState.Loading)
                }
                eff(
                    PaginationFeature.Eff.Load(
                        page = state.pagesLoaded + 1,
                        size = PAGE_SIZE
                    )
                )
            }
        }

        PaginationFeature.Msg.RetryLoadNext -> {
            if (state.nextPageLoadingState is PaginationFeature.State.PageLoadingState.Error) {
                state {
                    copy(nextPageLoadingState = PaginationFeature.State.PageLoadingState.Loading)
                }
                eff(
                    PaginationFeature.Eff.Load(
                        page = state.pagesLoaded + 1,
                        size = PAGE_SIZE
                    )
                )
            }
        }

        is PaginationFeature.Msg.Internal.LoadResult<*> -> {
            msg.result
                .onSuccess { resultList ->
                    // ignoring old requests
                    if (msg.requestedPage == state.pagesLoaded + 1) {
                        state {
                            PaginationFeature.State(
                                items + resultList as List<T>,
                                pagesLoaded = pagesLoaded + 1,
                                nextPageLoadingState = PaginationFeature.State.PageLoadingState.Idle
                            )
                        }
                    }
                }
                .onFailure {
                    state { copy(nextPageLoadingState = PaginationFeature.State.PageLoadingState.Error(it)) }
                }
        }
    }
}