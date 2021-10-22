package com.mvi.core.repositories

import com.mvi.core.entities.Item

interface ItemsRepository {
    suspend fun loadAllItems(): List<Item>? = throw NotImplementedError()
}