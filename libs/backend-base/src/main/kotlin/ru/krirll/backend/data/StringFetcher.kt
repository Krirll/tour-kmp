package ru.krirll.backend.data

import org.koin.core.annotation.Singleton

@Singleton
class StringFetcher {
    fun get(resource: StringResource): String {
        return resource.message
    }
}

enum class StringResource(val message: String) {
    UNKNOWN_USER("Пользователь не найден или неверный пароль"),
    INVALID_PASSWORD("Вы ввели неверный пароль"),
    NO_CHANGES_PASS("Старый и новый пароль должны отличаться"),
    USER_BLOCKED("Пользователь заблокирован"),
    REG_BLOCKED("Регистрация недоступна. Доступ по приглашениям."),
    VIDEO_NOT_FOUND("Видео не найдено.")
}
