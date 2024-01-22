package com.github.ikarenkov.sample.shikimori.impl.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.github.ikarenkov.sample.shikimori.impl.auth.data.AccessTokenResponse
import com.github.ikarenkov.sample.shikimori.impl.data.AuthDataLocalStorage
import com.github.ikarenkov.sample.shikimori.impl.data.ShikimoriBackendApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.ikarenkov.kombucha.eff_handler.FlowEffectHandler
import ru.ikarenkov.kombucha.eff_handler.adaptCast
import ru.ikarenkov.kombucha.reducer.dslReducer
import ru.ikarenkov.kombucha.store.Store
import ru.ikarenkov.kombucha.store.StoreFactory

internal class AuthFeature(
    storeFactory: StoreFactory,
    authEffectHandler: AuthEffHandler
) : Store<AuthFeature.Msg, AuthFeature.State, AuthFeature.Eff> by storeFactory.create(
    name = "AuthFeature",
    initialState = State.NotAuthorized,
    reducer = Reducer::invoke,
    effectHandlers = arrayOf(authEffectHandler.adaptCast()),
) {

    sealed interface State {

        data class Init(val inProgress: Boolean = false) : State

        data class Authorized(
            val accessToken: String,
            val refreshToken: String,
        ) : State

        data object NotAuthorized : State

        data class OAuthInProgress(
            val authorizationCode: String
        ) : State

    }

    sealed interface Msg {

        // For loading cached data to memory
        data object Init : Msg

        data object Auth : Msg
        data object RefreshToken : Msg

        data class AuthResult(val authData: State.Authorized) : Msg
        data class LoadCacheAuthResult(val accessTokenResponse: AccessTokenResponse?) : Msg

        data class OAuthResult(
            val oauthUrl: String
        ) : Msg

    }

    sealed interface Eff {

        data object GetAuthorizationCode : Eff
        data class UpdateToken(val refreshToken: String) : Eff
        data object LoadCachedData : Eff
        sealed interface GetAccessToken : Eff {
            data class AuthorizationCode(val code: String) : GetAccessToken
            data class RefreshToken(val token: String) : GetAccessToken
        }

    }

    internal object Reducer : ru.ikarenkov.kombucha.reducer.Reducer<Msg, State, Eff> by dslReducer({ msg ->
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
                    eff(Eff.GetAuthorizationCode)
                }
            }
            is Msg.AuthResult -> {
                state { msg.authData }
            }
            is Msg.OAuthResult -> {
                val code = msg.oauthUrl.substringAfter("kombucha.shikimori://oauth?code=")
                if (code.isNotEmpty()) {
                    state { State.OAuthInProgress(code) }
                    eff(Eff.GetAccessToken.AuthorizationCode(code))
                }
            }
            is Msg.LoadCacheAuthResult -> {
                if (initialState is State.Init) {
                    if (msg.accessTokenResponse == null) {
                        state { State.NotAuthorized }
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
                    is State.Authorized -> eff(Eff.UpdateToken(state.refreshToken))
                    is State.NotAuthorized -> eff(Eff.GetAuthorizationCode)
                    else -> {
                        // ignored
                    }
                }
            }
        }
    })

    internal class AuthEffHandler(
        private val context: Context,
        private val shikimoriApi: ShikimoriBackendApi,
        private val localStorage: AuthDataLocalStorage
    ) : FlowEffectHandler<Eff, Msg> {

        override fun handleEff(eff: Eff): Flow<Msg> = when (eff) {
            is Eff.GetAccessToken.AuthorizationCode -> flow {
                val response = shikimoriApi.getAccessToken(eff.code).getOrThrow()
                localStorage.saveAccessTokens(response)
                val msg = Msg.AuthResult(
                    State.Authorized(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                )
                emit(msg)
            }
            is Eff.GetAccessToken.RefreshToken -> TODO()
            is Eff.LoadCachedData -> flow {
                emit(Msg.LoadCacheAuthResult(localStorage.getAccessTokensResponse()))
                shikimoriApi.invalidateBearerTokens()
            }
            Eff.GetAuthorizationCode -> flow {
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
            is Eff.UpdateToken -> flow {
                val response = shikimoriApi.refreshTokens(eff.refreshToken).getOrThrow()
                localStorage.saveAccessTokens(response)
                shikimoriApi.invalidateBearerTokens()
                val msg = Msg.AuthResult(
                    State.Authorized(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                )
                emit(msg)
            }
        }

    }
}