package com.mvi.core.repositories

import com.mvi.core.entities.Cart

interface CartRepository {
    suspend fun loadCart(): Cart = throw NotImplementedError()
    suspend fun saveCart(cart: Cart): Cart = throw NotImplementedError()
}