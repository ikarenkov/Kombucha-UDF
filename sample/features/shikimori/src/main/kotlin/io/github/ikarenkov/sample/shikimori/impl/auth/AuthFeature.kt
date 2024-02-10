package io.github.ikarenkov.sample.shikimori.impl.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import io.github.ikarenkov.kombucha.eff_handler.EffectHandler
import io.github.ikarenkov.kombucha.eff_handler.adaptCast
import io.github.ikarenkov.kombucha.reducer.dslReducer
import io.github.ikarenkov.kombucha.store.Store
import io.github.ikarenkov.kombucha.store.StoreFactory
import io.github.ikarenkov.sample.shikimori.impl.auth.data.AccessTokenResponse
import io.github.ikarenkov.sample.shikimori.impl.data.AuthDataLocalStorage
import io.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

internal class AuthStore(
    storeFactory: StoreFactory,
    authEffectHandler: AuthFeature.AuthEffHandler
) : Store<AuthFeature.Msg, AuthFeature.State, AuthFeature.Eff> by storeFactory.create(
    name = "AuthFeature",
    initialState = AuthFeature.State.Init(false),
    reducer = AuthFeature.Reducer,
    effectHandlers = arrayOf(authEffectHandler.adaptCast()),
) {
    init {
        accept(AuthFeature.Msg.Init)
    }
}

internal object AuthFeature {

    sealed interface State {

        data class Init(val inProgress: Boolean = false) : State

        data class Authorized(
            val accessToken: String,
            val refreshToken: String,
            // need to distinguish states when we have auth data
            val failedRefreshAccessToken: Boolean = false
        ) : State

        sealed interface NotAuthorized : State {
            data class OAuthInProgress(
                val authorizationCode: String
            ) : NotAuthorized

            data object Idle : NotAuthorized
        }

    }

    sealed interface Msg {

        // For loading cached data to memory
        data object Init : Msg

        data object Auth : Msg
        data object RefreshToken : Msg

        data class AuthResult(val result: Result<State.Authorized>) : Msg
        data class LoadCacheAuthResult(val accessTokenResponse: AccessTokenResponse?) : Msg

        data class OAuthResult(
            val oauthUrl: String
        ) : Msg

    }

    sealed interface Eff {

        data object GetAuthorizationCodeBrouser : Eff
        data object LoadCachedData : Eff
        sealed interface GetAccessToken : Eff {
            data class AuthorizationCode(val code: String) : GetAccessToken
            data class RefreshToken(val refreshToken: String) : GetAccessToken
        }

        data class OAuthErrorCodeIsEmpty(
            val oauthUrl: String
        ) : Eff

    }

    internal object Reducer : io.github.ikarenkov.kombucha.reducer.Reducer<Msg, State, Eff> by dslReducer({ msg ->
        when (msg) {
            Msg.Init -> {
                val state = state
                if (state is State.Init && !state.inProgress) {
                    state { State.Init(inProgress = true) }
                    eff(Eff.LoadCachedData)
                }
            }
            Msg.Auth -> {
                if (state is State.NotAuthorized) {
                    eff(Eff.GetAuthorizationCodeBrouser)
                }
            }
            is Msg.AuthResult -> {
                msg.result.fold(
                    onSuccess = {
                        state { it }
                    },
                    onFailure = {
                        when (val state = state) {
                            is State.NotAuthorized -> state { State.NotAuthorized.Idle }
                            is State.Authorized -> {
                                state { state.copy(failedRefreshAccessToken = true) }
                            }
                            is State.Init -> {}
                        }
                    }
                )
            }
            is Msg.OAuthResult -> {
                val code = msg.oauthUrl.substringAfter("kombucha.shikimori://oauth?code=")
                if (code.isNotEmpty()) {
                    state { State.NotAuthorized.OAuthInProgress(code) }
                    eff(Eff.GetAccessToken.AuthorizationCode(code))
                } else {
                    eff(Eff.OAuthErrorCodeIsEmpty(msg.oauthUrl))
                    state { State.NotAuthorized.Idle }
                }
            }
            is Msg.LoadCacheAuthResult -> {
                if (initialState is State.Init) {
                    if (msg.accessTokenResponse == null) {
                        state { State.NotAuthorized.Idle }
                    } else {
                        state {
                            State.Authorized(
                                accessToken = msg.accessTokenResponse.accessToken,
                                refreshToken = msg.accessTokenResponse.refreshToken
                            )
                        }
                    }
                }
            }
            Msg.RefreshToken -> {
                val state = state
                when (state) {
                    is State.Authorized -> eff(Eff.GetAccessToken.RefreshToken(state.refreshToken))
                    // meybe it would be better to send effect out the fact, that we should inform user for auth?
                    is State.NotAuthorized -> eff(Eff.GetAuthorizationCodeBrouser)
                    is State.Init -> Unit
                }
            }
        }
    })

    internal class AuthEffHandler(
        private val context: Context,
        private val shikimoriApi: ShikimoriBackendApi,
        private val localStorage: AuthDataLocalStorage
    ) : EffectHandler<Eff, Msg> {

        override fun handleEff(eff: Eff): Flow<Msg> = when (eff) {
            is Eff.GetAccessToken -> getAccessToken(eff)
            is Eff.LoadCachedData -> flow {
                emit(Msg.LoadCacheAuthResult(localStorage.getAccessTokensResponse()))
                shikimoriApi.invalidateBearerTokens()
            }
            Eff.GetAuthorizationCodeBrouser -> flow {
                CustomTabsIntent.Builder().build().run {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    launchUrl(
                        context,
                        Uri.parse(
                            "https://shikimori.one/oauth/authorize?" +
                                    "client_id=Ty0gZ-tS9LCWcuRov-a2rLvkHqE7kcYkgwelAdmxxIk" +
                                    "&redirect_uri=kombucha.shikimori%3A%2F%2Foauth" +
                                    "&response_type=code" +
                                    "&scope=user_rates+comments+topics"
                        )
                    )
                }
            }
            is Eff.OAuthErrorCodeIsEmpty -> emptyFlow()
        }

        private fun getAccessToken(eff: Eff.GetAccessToken) = flow {
            val result = when (eff) {
                is Eff.GetAccessToken.AuthorizationCode -> shikimoriApi.getAccessToken(eff.code)
                is Eff.GetAccessToken.RefreshToken -> shikimoriApi.refreshTokens(eff.refreshToken)
            }
            result.onSuccess {
                localStorage.saveAccessTokens(it)
            }
            if (eff is Eff.GetAccessToken.AuthorizationCode) {
                shikimoriApi.invalidateBearerTokens()
            }
            val msg = Msg.AuthResult(
                result.map { response ->
                    State.Authorized(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                }
            )
            emit(msg)
        }

    }
}