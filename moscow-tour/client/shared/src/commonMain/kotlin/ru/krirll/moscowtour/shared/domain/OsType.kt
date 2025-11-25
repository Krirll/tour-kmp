package ru.krirll.moscowtour.shared.domain

enum class OsType {
    ANDROID,
    JS
}

expect fun getCurrentOsType(): OsType
