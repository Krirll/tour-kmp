package ru.krirll.moscowtour.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Tour(
    val id: Long,
    val title: String,
    val description: String,
    val city: String,
    val country: String,
    val dateBegin: Long,
    val dateEnd: Long,
    val canBuy: Boolean,
    val price: Double,
    val imagesUrls: List<String>?
)
