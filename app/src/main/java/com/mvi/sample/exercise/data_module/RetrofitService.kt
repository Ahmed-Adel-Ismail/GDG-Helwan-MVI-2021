package com.mvi.sample.exercise.data_module

import com.mvi.sample.exercise.core_module.TableRow

val retrofitService by lazy {
    object : RetrofitService{}
}

interface RetrofitService {

    suspend fun getTable() = listOf(
        TableRow("ONE", 1),
        TableRow("TWO", 2),
        TableRow("THREE", 3),
        TableRow("FOUR", 4),
        TableRow("FIVE", 5),

    )

}