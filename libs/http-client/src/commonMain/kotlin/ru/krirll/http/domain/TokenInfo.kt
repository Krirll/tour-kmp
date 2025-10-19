package ru.krirll.http.domain

import kotlinx.serialization.Serializable

@Serializable
data class TokenInfo(
    val token: String,
    val refresh: String? = null
)
