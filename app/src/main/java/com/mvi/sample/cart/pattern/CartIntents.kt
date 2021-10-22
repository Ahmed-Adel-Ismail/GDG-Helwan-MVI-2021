package com.mvi.sample.cart.pattern

import com.mvi.core.entities.Item

/**
 * these are the intents triggered by the UI (view function) and passed to the model function
 */
sealed class CartIntents(open val viewState: CartViewState? = null) {
    object Initialize : CartIntents()
    data class AddItem(override val viewState: CartViewState, val item: Item) : CartIntents()
    data class RemoveItem(override val viewState: CartViewState, val item: Item) : CartIntents()
    data class ErrorDisplayed(override val viewState: CartViewState) : CartIntents()
}