package com.mvi.sample.exercise.data_module

import com.mvi.sample.exercise.core_module.Table
import com.mvi.sample.exercise.core_module.TableRepository

class TableRepositoryImplementer : TableRepository {
    override suspend fun getTable(): Table = retrofitService.getTable()
}