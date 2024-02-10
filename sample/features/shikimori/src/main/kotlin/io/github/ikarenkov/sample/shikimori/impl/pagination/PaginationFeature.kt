package io.github.ikarenkov.sample.shikimori.impl.pagination

import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory

class PaginationStore<T>(
    name: String,
    storeFactory: StoreFactory,
    dataFetcher: PaginationEffectHandler.DataFetcher<T>
) : Store<PaginationFeature.Msg, PaginationFeature.State<T>, PaginationFeature.Eff> by storeFactory.create(
    name = name,
    initialState = PaginationFeature.State.Initial(),
    reducer = PaginationFeature.reducer(),
    initialEffects = PaginationFeature.Eff.Initial(),
    PaginationEffectHandler(dataFetcher).adaptCast()
)

object PaginationFeature {

    const val PAGE_SIZE = 10

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

            fun Initial() = setOf(Load(1, PAGE_SIZE))

        }

    }

    fun <T> reducer(): Reducer<Msg, State<T>, Eff> = dslReducer { msg ->
        when (msg) {
            Msg.LoadNext -> {
                if (state.nextPageLoadingState == State.PageLoadingState.Idle) {
                    state {
                        copy(nextPageLoadingState = State.PageLoadingState.Loading)
                    }
                    eff(
                        Eff.Load(
                            page = state.pagesLoaded + 1,
                            size = PAGE_SIZE
                        )
                    )
                }
            }
            Msg.RetryLoadNext -> {
                if (state.nextPageLoadingState is State.PageLoadingState.Error) {
                    state {
                        copy(nextPageLoadingState = State.PageLoadingState.Loading)
                    }
                    eff(
                        Eff.Load(
                            page = state.pagesLoaded + 1,
                            size = PAGE_SIZE
                        )
                    )
                }
            }
            is Msg.Internal.LoadResult<*> -> {
                msg.result
                    .onSuccess { resultList ->
                        // ignoring old requests
                        if (msg.requestedPage == state.pagesLoaded + 1) {
                            state {
                                State(
                                    items + resultList as List<T>,
                                    pagesLoaded = pagesLoaded + 1,
                                    nextPageLoadingState = State.PageLoadingState.Idle
                                )
                            }
                        }
                    }
                    .onFailure {
                        state { copy(nextPageLoadingState = State.PageLoadingState.Error(it)) }
                    }
            }
        }
    }

}