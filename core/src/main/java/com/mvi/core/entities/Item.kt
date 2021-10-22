package com.mvi.core.entities

data class Item(
    val id: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val subItems: List<Item>? = null,
    val suggestions: List<Item>? = null
)