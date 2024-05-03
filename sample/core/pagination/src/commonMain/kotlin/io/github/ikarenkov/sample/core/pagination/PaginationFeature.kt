package io.github.ikarenkov.sample.core.pagination

import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.Reducer
import io.github.ikarenkov.kombucha.reducer.ResultBuilder
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.ReducerStoreFactory
import io.github.ikarenkov.kombucha.store.Store

open class PaginationStore<Item>(
    name: String,
    reducerStoreFactory: ReducerStoreFactory,
    dataFetcher: PaginationDataFetcher<Item>,
    pageSize: Int = PaginationFeature.DEFAULT_PAGE_SIZE,
) : Store<PaginationMsg<Item>, PaginationState<Item>, PaginationEff> by reducerStoreFactory.create(
    name = name,
    initialState = PaginationState.Initial(pageSize),
    reducer = PaginationFeature.reducer(),
    initialEffects = PaginationEff.Initial(pageSize),
    PaginationEffectHandler(dataFetcher).adaptCast()
)

object PaginationFeature {

    const val DEFAULT_PAGE_SIZE = 10

    fun <Item> reducer(): Reducer<PaginationMsg<Item>, PaginationState<Item>, PaginationEff> = dslReducer { msg ->
        when (msg) {
            PaginationMsg.Outer.LoadNext -> loadNext()
            PaginationMsg.Outer.RetryLoadNext -> retryLoadNext()
            is PaginationMsg.Inner.LoadResult<Item> -> consumeLoadResult(msg)
            PaginationMsg.Outer.Reload -> {
                TODO()
            }
            is PaginationMsg.Outer.UpdateItem -> updateItem<Item>(msg)
            is PaginationMsg.Outer.RemoveItem -> {
                state {
                    copy(items = items.filterNot(msg.condition))
                }
            }
            is PaginationMsg.Outer.UpdateItems -> {
                state {
                    copy(items = items.map { msg.update(it) })
                }
            }
            is PaginationMsg.Outer.AddItem -> {
                state {
                    copy(
                        items = items.toMutableList().apply {
                            add(index = msg.pos, element = msg.item)
                        }
                    )
                }
            }
        }
    }

    private fun <Item> ResultBuilder<PaginationState<Item>, PaginationEff>.consumeLoadResult(
        msg: PaginationMsg.Inner.LoadResult<Item>
    ) {
        msg.result
            .onSuccess { response ->
                // ignoring old requests
                if (msg.requestedPage == state.pagesLoaded + 1) {
                    state {
                        copy(
                            items = items + response.items as List<Item>,
                            pagesLoaded = pagesLoaded + 1,
                            totalPages = response.totalPages,
                            nextPageLoadingState = PaginationState.PageLoadingState.Idle
                        )
                    }
                }
            }
            .onFailure {
                state { copy(nextPageLoadingState = PaginationState.PageLoadingState.Error(it)) }
            }
    }

    private fun <Item> ResultBuilder<PaginationState<Item>, PaginationEff>.updateItem(
        msg: PaginationMsg.Outer.UpdateItem<Item>,
    ) {
        if (msg.pos in state.items.indices) {
            state {
                val newItems = items.toMutableList()
                newItems[msg.pos] = msg.update(items[msg.pos])
                copy(items = newItems)
            }
        } else {
            // TODO: Logging
        }
    }

    private fun <Item> ResultBuilder<PaginationState<Item>, PaginationEff>.retryLoadNext() {
        if (state.nextPageLoadingState is PaginationState.PageLoadingState.Error) {
            state {
                copy(nextPageLoadingState = PaginationState.PageLoadingState.Loading)
            }
            eff(
                PaginationEff.Load(
                    page = state.pagesLoaded + 1,
                    size = DEFAULT_PAGE_SIZE
                )
            )
        }
    }

    private fun <Item> ResultBuilder<PaginationState<Item>, PaginationEff>.loadNext() {
        if (
            state.nextPageLoadingState == PaginationState.PageLoadingState.Idle &&
            !state.allLoaded
        ) {
            state {
                copy(nextPageLoadingState = PaginationState.PageLoadingState.Loading)
            }
            eff(
                PaginationEff.Load(
                    page = state.pagesLoaded + 1,
                    size = DEFAULT_PAGE_SIZE
                )
            )
        }
    }

}

sealed interface PaginationMsg<out Item> {

    sealed interface Outer<out Item> : PaginationMsg<Item> {

        data object LoadNext : Outer<Nothing>
        data object RetryLoadNext : Outer<Nothing>

        data object Reload : Outer<Nothing>

        data class AddItem<out Item>(
            val item: Item,
            val pos: Int = 0
        ) : Outer<Item>

        data class RemoveItem<Item>(
            val condition: (Item) -> Boolean
        ) : Outer<Item>

        data class UpdateItem<Item>(
            val pos: Int,
            val update: (Item) -> Item
        ) : Outer<Item>

        data class UpdateItems<Item>(
            val update: (Item) -> Item
        ) : Outer<Item>
        // TODO: data object CancelLoad
    }

    sealed interface Inner<out T> : PaginationMsg<T> {
        data class LoadResult<out T>(
            val result: Result<PaginationDataFetcher.Response<T>>,
            val requestedPage: Int,
            val requestedSize: Int
        ) : Inner<T>
    }

//        data object Clear : Msg

}

data class PaginationState<out T>(
    val pageSize: Int,
    val items: List<T>,
    val pagesLoaded: Int,
    /**
     * if null, that we don't know the total pages count, but we know that there are more pages.
     */
    val totalPages: Int?,
    val nextPageLoadingState: PageLoadingState,
) {

    // TODO: empty list case
    val allLoaded = totalPages != null &&
        pagesLoaded == totalPages && pagesLoaded != 0

    val hadSuccessLoading = pagesLoaded > 0

    sealed interface PageLoadingState {
        data object Idle : PageLoadingState
        data object Loading : PageLoadingState
        data class Error(val throwable: Throwable) : PageLoadingState
    }

    companion object {

        fun <T> Initial(pageSize: Int) = PaginationState(
            pageSize = pageSize,
            items = emptyList<T>(),
            pagesLoaded = 0,
            totalPages = null,
            nextPageLoadingState = PageLoadingState.Idle
        )

    }

}

sealed interface PaginationEff {

    data class Load(val page: Int, val size: Int) : PaginationEff
    data class CancelLoad(val page: Int, val size: Int) : PaginationEff

    companion object {

        fun Initial(pageSize: Int) = setOf(Load(1, pageSize))

    }

}