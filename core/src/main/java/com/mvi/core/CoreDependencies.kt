package com.mvi.core

import com.mvi.core.repositories.CartRepository
import com.mvi.core.repositories.ItemsRepository

object CoreDependencies {
    var cartRepository: CartRepository = object : CartRepository {}
    var itemsRepository: ItemsRepository = object : ItemsRepository {}
}