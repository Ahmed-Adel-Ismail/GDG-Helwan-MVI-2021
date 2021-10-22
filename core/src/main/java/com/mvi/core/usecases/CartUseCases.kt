package com.mvi.core.usecases

import com.mvi.core.CoreDependencies
import com.mvi.core.entities.Cart
import com.mvi.core.entities.Item
import com.mvi.core.repositories.CartRepository

object CartUseCases

suspend fun CartUseCases.addItemToCart(
    item: Item,
    repository: CartRepository = CoreDependencies.cartRepository
) = repository.saveCart(cartWithNewItem(repository.loadCart(), item))

private fun cartWithNewItem(
    cart: Cart,
    item: Item
) = cart.copy(items = cart.addItem(item))

private fun Cart.addItem(item: Item): MutableList<Item> {
    val cartItems = items?.toMutableList() ?: mutableListOf()
    cartItems += item
    return cartItems
}

suspend fun CartUseCases.removeItemFromCart(
    item: Item,
    repository: CartRepository = CoreDependencies.cartRepository
) = repository.saveCart(cartWithRemovedItem(repository.loadCart(), item))


private fun cartWithRemovedItem(
    cart: Cart,
    item: Item
) = cart.copy(items = cart.removeItem(item))

private fun Cart.removeItem(item: Item) = items?.toMutableList()?.also { it.remove(item) }

suspend fun CartUseCases.loadCart(
    cartRepository: CartRepository = CoreDependencies.cartRepository
) = cartRepository.loadCart()