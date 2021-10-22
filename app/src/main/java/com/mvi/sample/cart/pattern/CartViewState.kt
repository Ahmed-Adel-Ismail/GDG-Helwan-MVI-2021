package com.mvi.sample.cart.pattern

import com.mvi.core.entities.Cart
import com.mvi.sample.cart.ItemViewData
import kotlinx.coroutines.Job

/**
 * this is the UI model returned by the model function
 */
data class CartViewState(
    val cart: Cart? = null,
    val itemsViewData: List<ItemViewData>? = null,
    val progress: Job? = null,
    val error: Throwable? = null
)