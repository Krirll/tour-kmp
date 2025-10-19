package ru.krirll.moscowtour.shared.domain

enum class OsType {
    JVM,
    ANDROID,
    IOS,
    JS
}

expect fun getCurrentOsType(): OsType

val isJs: Boolean get() = getCurrentOsType() == OsType.JS
