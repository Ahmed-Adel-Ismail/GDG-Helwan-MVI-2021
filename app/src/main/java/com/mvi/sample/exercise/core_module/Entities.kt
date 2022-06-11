package com.mvi.sample.exercise.core_module

typealias Table = List<TableRow>

data class TableRow(
    val title: String? = null,
    val number: Int? = null
)