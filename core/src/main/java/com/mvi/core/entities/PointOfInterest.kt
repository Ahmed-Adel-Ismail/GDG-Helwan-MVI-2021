package com.mvi.core.entities

data class PointOfInterest(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val locationInfo: LocationInfo
)