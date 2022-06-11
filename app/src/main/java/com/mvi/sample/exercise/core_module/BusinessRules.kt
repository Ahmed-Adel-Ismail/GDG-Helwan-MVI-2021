package com.mvi.sample.exercise.core_module

import com.mvi.sample.exercise.data_module.TableRepositoryImplementer

@DslMarker
annotation class BusinessRule

@BusinessRule
suspend fun requestTable(repository: TableRepository = TableRepositoryImplementer()) = repository.getTable()