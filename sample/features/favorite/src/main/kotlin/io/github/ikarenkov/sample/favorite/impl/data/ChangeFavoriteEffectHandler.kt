package io.github.ikarenkov.sample.favorite.impl.data

import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.eff_handler.adapt
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.Eff
import io.github.ikarenkov.sample.favorite.impl.FavoriteFeature.Msg
import kotlinx.coroutines.flow.Flow

internal class ChangeFavoriteEffectHandler : EffectHandler<ChangeFavoriteEff, ChangeFavoriteMsg> {
    override fun handleEff(eff: ChangeFavoriteEff): Flow<ChangeFavoriteMsg> {
        TODO("Not yet implemented")
    }
}

internal data class ChangeFavoriteEff(val id: String, val favorite: Boolean)

internal sealed interface ChangeFavoriteMsg {
    val id: String
    val favorite: Boolean

    data class Done(
        override val id: String,
        override val favorite: Boolean
    ) : ChangeFavoriteMsg

    data class Error(
        override val id: String,
        override val favorite: Boolean,
        val throwable: Throwable?
    ) : ChangeFavoriteMsg
}

internal fun ChangeFavoriteEffectHandler.adapt() = adapt(
    effAdapter = { eff: Eff ->
        when (eff) {
            is Eff.Inner.RemoveItem -> ChangeFavoriteEff(eff.id, favorite = false)
            else -> null
        }
    },
    msgAdapter = { msg ->
        when (msg) {
            is ChangeFavoriteMsg.Done ->
                Msg.Inner.ItemRemoveResult.Done(msg.id)
            is ChangeFavoriteMsg.Error ->
                Msg.Inner.ItemRemoveResult.Error(msg.id, msg.throwable)
        }

    }
)