package com.mvi.core.entities

data class Cart(
    val items: List<Item>? = null,
    val totalPrice: Double? = null,
    val totalQuantity: Double? = null
)