package com.mvi.sample

import com.mvi.core.entities.Cart
import com.mvi.core.entities.Item
import com.mvi.core.usecases.CartUseCases
import com.mvi.core.usecases.addItemToCart
import com.mvi.core.usecases.loadCart
import com.mvi.core.usecases.removeItemFromCart
import com.mvi.sample.cart.ItemViewData
import com.mvi.sample.cart.pattern.CartIntents
import com.mvi.sample.cart.pattern.CartIntents.*
import com.mvi.sample.cart.pattern.CartViewState

/**
 * this is the model function in MVI, it's responsibility is to convert intents into views states
 */
suspend fun mapIntentToViewState(
    intent: CartIntents,
    loadCart: suspend () -> Cart = { CartUseCases.loadCart() },
    addItem: suspend (Item) -> Cart = { CartUseCases.addItemToCart(it) },
    removeItem: suspend (Item) -> Cart = { CartUseCases.removeItemFromCart(it) }
) = when (intent) {
    is Initialize -> onInitialize(loadCart)
    is AddItem -> onAddItem(intent, addItem)
    is RemoveItem -> onRemoveItem(intent, removeItem)
    is ErrorDisplayed -> intent.viewState.copy(error = null)
}

private suspend fun onInitialize(loadCart: suspend () -> Cart): CartViewState {
    return runCatching { proceedWithInitialize(loadCart) }
        .getOrElse { CartViewState(error = it) }
}

private suspend fun proceedWithInitialize(loadCart: suspend () -> Cart): CartViewState {
    val updatedCart = loadCart()
    val itemsViewData = updatedCart.items?.toItemViewData() ?: listOf()
    return CartViewState(cart = updatedCart, itemsViewData = itemsViewData)
}

private suspend fun onAddItem(
    intent: AddItem,
    addItem: suspend (Item) -> Cart
) = runCatching { proceedWithAddItem(intent, addItem) }
    .getOrElse { intent.viewState.copy(error = it) }


private suspend fun proceedWithAddItem(
    intent: AddItem,
    addItem: suspend (Item) -> Cart
): CartViewState {
    val updatedCart = addItem(intent.item)
    val itemsViewData = updatedCart.items?.toItemViewData() ?: listOf()
    return intent.viewState.copy(cart = updatedCart, itemsViewData = itemsViewData)
}

private suspend fun onRemoveItem(
    intent: RemoveItem,
    removeItem: suspend (Item) -> Cart
) = runCatching { proceedWithRemoveItem(intent, removeItem) }
    .getOrElse { intent.viewState.copy(error = it) }

private suspend fun proceedWithRemoveItem(
    intent: RemoveItem,
    removeItem: suspend (Item) -> Cart
): CartViewState {
    val updatedCart = removeItem(intent.item)
    val itemsViewData = updatedCart.items?.toItemViewData() ?: listOf()
    return intent.viewState.copy(cart = updatedCart, itemsViewData = itemsViewData)
}

internal fun List<Item>.toItemViewData(): List<ItemViewData> = groupBy { it }
    .map { ItemViewData(it.key, it.value.size) }
