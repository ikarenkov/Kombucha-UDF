package io.github.ikarenkov.kombucha.eff_handler

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ActionManagerTestEffectHandler : EffectHandler<ActionManagerTestEff, ActionManagerTestMsg> {

    private val actionManager = ActionManager()

    override fun handleEff(eff: ActionManagerTestEff): Flow<ActionManagerTestMsg> = when (eff) {
        is ActionManagerTestEff.CounterEff -> actionManager.recreateAction {
            flow {
                delay(100)
                emit(ActionManagerTestMsg.CounterMsg(eff.count))
            }
        }
    }

}

internal sealed interface ActionManagerTestEff {
    data class CounterEff(val count: Int) : ActionManagerTestEff
}

internal sealed interface ActionManagerTestMsg {
    data class CounterMsg(val count: Int) : ActionManagerTestMsg
}