package ru.krirll.backend.domain

interface LoginVerifier {
    suspend fun verify(login: String)
}

const val LOGIN_NAME = "login"
const val USER_ID_ARG = "user_id"

const val INVALID_TOKEN_MSG = "Был передан неправильный токен"
