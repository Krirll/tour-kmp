package ru.krirll.http.domain

interface LogoutAction {
    suspend fun logout(withDelete: Boolean = false)
}
