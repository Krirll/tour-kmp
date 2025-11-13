package ru.krirll.moscowtour.shared.domain

import kotlinx.serialization.Serializable
import ru.krirll.http.domain.TokenInfo
import ru.krirll.moscowtour.shared.domain.model.LoginInfo

interface AuthTokenRepository {

    suspend fun register(loginInfo: LoginInfo): TokenInfo
    suspend fun login(loginInfo: LoginInfo): TokenInfo
    suspend fun update(refresh: TokenRequest): TokenInfo
    suspend fun changePassword(request: ChangePasswordRequest)
    suspend fun revoke(tokenRequest: TokenRequest)
    suspend fun delete(tokenRequest: TokenRequest)

    companion object {
        const val LOGIN_PATH = "$DYNAMIC_PREFIX/login"
        const val REGISTER_PATH = "$DYNAMIC_PREFIX/register"
        const val UPDATE_PATH = "$DYNAMIC_PREFIX/updateToken"
        const val CHANGE_PASS_PATH = "$DYNAMIC_PREFIX/changePassword"
        const val REVOKE_PATH = "$DYNAMIC_PREFIX/revokeToken"
        const val DELETE_PATH = "$DYNAMIC_PREFIX/deleteAccount"
    }
}

@Serializable
data class TokenRequest(val token: String)

@Serializable
data class ChangePasswordRequest(
    val oldPasswordHash: String,
    val newPasswordHash: String
)
