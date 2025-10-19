package ru.krirll.backend.data

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.koin.core.annotation.Single
import ru.krirll.backend.domain.JwtInfo
import ru.krirll.backend.domain.LOGIN_NAME
import ru.krirll.backend.domain.USER_ID_ARG
import java.util.Date
import java.util.concurrent.TimeUnit

@Single
class JwtPreparer(private val jwtInfo: JwtInfo) {
    fun prepare(
        login: String,
        userId: Long
    ): String {
        val exp = Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30))
        val token = JWT.create()
            .withAudience(jwtInfo.audience)
            .withIssuer(jwtInfo.issuer)
            .withClaim(LOGIN_NAME, login)
            .withClaim(USER_ID_ARG, userId)
            .withSubject(userId.toString())
            .withExpiresAt(exp)
            .sign(Algorithm.HMAC512(jwtInfo.secret))
        return token
    }
}
