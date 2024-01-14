package com.github.ikarenkov.sample.shikimori.impl.pagination

import ru.ikarenkov.teamaker.eff_handler.adaptCast
import ru.ikarenkov.teamaker.reducer.Reducer
import ru.ikarenkov.teamaker.reducer.dslReducer
import ru.ikarenkov.teamaker.store.Store
import ru.ikarenkov.teamaker.store.StoreFactory

object PaginationFeature {

    fun <T> create(
        storeFactory: StoreFactory,
        name: String,
        dataFetcher: PaginationEffectHandler.DataFetcher<T>
    ): Store<Msg, State<T>, Eff> = storeFactory.create(
        name = name,
        initialState = State.Initial(),
        reducer = reducer<T>()::invoke,
        initEffects = Eff.Initial(),
        PaginationEffectHandler(dataFetcher).adaptCast()
    )

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

    class Item(val name: String)

    const val PAGE_SIZE = 10

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