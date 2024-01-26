package io.github.ikarenkov.kombucha.sample.core.feature

import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent
import kotlin.reflect.KClass

open class FeatureFacade<Deps : Any, Api : Any>(
    val depsClass: KClass<Deps>,
    val apiClass: KClass<Api>,
    private val featureScopeName: String,
    private val scopeSet: ScopeDSL.() -> Unit
) {

    val scope: Scope
        get() = KoinJavaComponent.getKoin().getScopeOrNull(featureScopeName)
            ?: KoinJavaComponent.getKoin().createScope(featureScopeName, TypeQualifier(apiClass)).also {
                loadKoinModules(
                    module {
                        scope(TypeQualifier(apiClass)) {
                            scopeSet()
                        }
                    }
                )
            }

    val api: Api by lazy { scope.get(apiClass) }

}

inline fun <reified Deps : Any, reified Api : Any> featureFacade(
    featureScopeName: String,
    noinline scopeSet: ScopeDSL.() -> Unit
) = object : FeatureFacade<Deps, Api>(
    Deps::class,
    Api::class,
    featureScopeName,
    scopeSet
) {}