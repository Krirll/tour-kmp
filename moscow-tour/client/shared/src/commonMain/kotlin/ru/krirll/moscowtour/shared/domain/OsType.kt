package ru.krirll.moscowtour.shared.domain

enum class OsType {
    ANDROID,
    IOS,
    JS
}

expect fun getCurrentOsType(): OsType

//todo придумать какое то ограничение, чтоб не перегружать сервак
val isJs: Boolean get() = getCurrentOsType() == OsType.JS
