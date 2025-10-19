package ru.krirll.backend.domain

data class JwtInfo(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)
