package ru.krirll.moscowtour.shared.domain

import ru.krirll.http.domain.TokenStorage
import ru.krirll.http.domain.hasAccount

suspend fun TokenStorage.use(
    active: suspend () -> Unit,
    fallback: suspend (Exception?) -> Unit
) {
    var fallbackCalled = false
    if (hasAccount()) {
        try {
            active()
        } catch (e: Exception) {
            fallbackCalled = true
            fallback(e)
        }
    }
    if (!fallbackCalled) {
        fallback(null)
    }
}

suspend fun <T> TokenStorage.query(
    active: suspend () -> T,
    fallback: suspend () -> T
): T {
    return if (hasAccount()) {
        try {
            active()
        } catch (e: Exception) {
            fallback()
        }
    } else {
        fallback()
    }
}
