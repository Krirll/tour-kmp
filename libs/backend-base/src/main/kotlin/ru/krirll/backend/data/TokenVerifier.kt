package ru.krirll.backend.data

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.koin.core.annotation.Factory
import ru.krirll.backend.domain.JwtInfo
import ru.krirll.backend.domain.LOGIN_NAME
import ru.krirll.backend.domain.LoginVerifier

@Factory
class TokenVerifier(
    private val jwtInfo: JwtInfo,
    private val loginVerifier: LoginVerifier
) {

    val verifier = JWT.require(Algorithm.HMAC512(jwtInfo.secret))
        .withAudience(jwtInfo.audience)
        .withIssuer(jwtInfo.issuer)
        .build()

    suspend fun verify(token: String): DecodedJWT {
        val jwt = verifier.verify(token)
        loginVerifier.verify(jwt.getClaim(LOGIN_NAME).asString())
        return jwt
    }
}
