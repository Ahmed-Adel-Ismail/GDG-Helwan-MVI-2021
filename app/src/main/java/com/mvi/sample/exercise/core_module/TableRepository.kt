package com.mvi.sample.exercise.core_module

interface TableRepository {
    suspend fun getTable() : Table
}
