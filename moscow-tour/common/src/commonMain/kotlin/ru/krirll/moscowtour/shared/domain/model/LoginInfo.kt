package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(
    val login: String,
    val passwordHash: String
)

open class LoginException(override val message: String?) : IllegalStateException()

class EmptyLoginException : LoginException("Login must be not empty!")
class EmptyPasswordException : LoginException("Password must be not empty!")
class ServerLoginException(message: String) : LoginException(message)
class UnknownLoginException(override val cause: Throwable?) : LoginException(cause?.message)
