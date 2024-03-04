package io.github.ikarenkov.sample.core.pagination

import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory

open class PaginationStore<T>(
    name: String,
    storeFactory: StoreFactory,
    dataFetcher: PaginationEffectHandler.DataFetcher<T>
) : Store<PaginationMsg, PaginationState<T>, PaginationEff> by storeFactory.create(
    name = name,
    initialState = PaginationState.Initial(),
    reducer = PaginationFeature.reducer(),
    initialEffects = PaginationEff.Initial(),
    PaginationEffectHandler(dataFetcher).adaptCast()
)

object PaginationFeature {

    const val PAGE_SIZE = 10

    fun <T> reducer(): Reducer<PaginationMsg, PaginationState<T>, PaginationEff> = dslReducer { msg ->
        when (msg) {
            PaginationMsg.Outer.LoadNext -> {
                if (state.nextPageLoadingState == PaginationState.PageLoadingState.Idle) {
                    state {
                        copy(nextPageLoadingState = PaginationState.PageLoadingState.Loading)
                    }
                    eff(
                        PaginationEff.Load(
                            page = state.pagesLoaded + 1,
                            size = PAGE_SIZE
                        )
                    )
                }
            }
            PaginationMsg.Outer.RetryLoadNext -> {
                if (state.nextPageLoadingState is PaginationState.PageLoadingState.Error) {
                    state {
                        copy(nextPageLoadingState = PaginationState.PageLoadingState.Loading)
                    }
                    eff(
                        PaginationEff.Load(
                            page = state.pagesLoaded + 1,
                            size = PAGE_SIZE
                        )
                    )
                }
            }
            is PaginationMsg.Inner.LoadResult<*> -> {
                msg.result
                    .onSuccess { resultList ->
                        // ignoring old requests
                        if (msg.requestedPage == state.pagesLoaded + 1) {
                            state {
                                PaginationState(
                                    items + resultList as List<T>,
                                    pagesLoaded = pagesLoaded + 1,
                                    nextPageLoadingState = PaginationState.PageLoadingState.Idle
                                )
                            }
                        }
                    }
                    .onFailure {
                        state { copy(nextPageLoadingState = PaginationState.PageLoadingState.Error(it)) }
                    }
            }
        }
    }

}

sealed interface PaginationMsg {

//        data object Reload : Msg

    sealed interface Outer : PaginationMsg {
        data object LoadNext : Outer
        data object RetryLoadNext : Outer
        // TODO: data object CancelLoad
    }

    sealed interface Inner : PaginationMsg {
        data class LoadResult<out T>(
            val result: Result<List<T>>,
            val requestedPage: Int,
            val requestedSize: Int
        ) : Inner
    }

//        data object Clear : Msg

}

data class PaginationState<out T>(
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

        fun <T> Initial() = PaginationState(
            emptyList<T>(),
            pagesLoaded = 0,
            nextPageLoadingState = PageLoadingState.Idle
        )

    }

}

sealed interface PaginationEff {

    data class Load(val page: Int, val size: Int) : PaginationEff
    data class CancelLoad(val page: Int, val size: Int) : PaginationEff

    companion object {

        fun Initial() = setOf(Load(1, PaginationFeature.PAGE_SIZE))

    }

}