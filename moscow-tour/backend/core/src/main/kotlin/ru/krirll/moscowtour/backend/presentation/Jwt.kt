package ru.krirll.moscowtour.backend.presentation

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import ru.krirll.backend.domain.INVALID_TOKEN_MSG
import ru.krirll.backend.domain.LOGIN_NAME
import ru.krirll.backend.domain.VerifyTokenException
import ru.krirll.moscowtour.backend.di.AuthEntryPoint
import ru.krirll.moscowtour.shared.domain.ACCOUNT_ID_ARG
import ru.krirll.moscowtour.shared.domain.model.ServerExceptionInfo

const val AUTH_JWT_NAME = "auth-jwt"

fun Application.configureJwt(authEntryPoint: AuthEntryPoint) {
    install(Authentication) {
        jwt(AUTH_JWT_NAME) {
            val jwtInfo = authEntryPoint.jwtInfo
            realm = jwtInfo.realm
            verifier(authEntryPoint.tokenVerifier.verifier)
            validate { credential ->
                val tokenRepo = authEntryPoint.loginVerifier
                val login = credential.payload.getClaim(LOGIN_NAME).asString()
                try {
                    if (login != null) {
                        tokenRepo.verify(login)
                        if (credential.payload.expiresAt == null) {
                            throw VerifyTokenException("Static tokens not supported!")
                        }
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                } catch (e: VerifyTokenException) {
                    routingLogger.error("verify token failed ${e.message}")
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ServerExceptionInfo(INVALID_TOKEN_MSG)
                )
            }
        }
    }
}

private fun String?.obtainToken(): DecodedJWT {
    val token = this?.removePrefix("Bearer ")
        ?: throw VerifyTokenException(INVALID_TOKEN_MSG)
    return JWT.decode(token)
}

fun ApplicationCall.obtainToken(): DecodedJWT {
    return request.headers["Authorization"].obtainToken()
}

fun ApplicationCall.obtainAccountId(): Long {
    return obtainToken()
        .getClaim(ACCOUNT_ID_ARG)
        .asLong()
}
