package com.mvi.sample.exercise.ui_module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mvi.sample.R
import com.mvi.sample.exercise.core_module.Table
import com.mvi.sample.exercise.core_module.requestTable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExerciseActivity : AppCompatActivity(), TableScreenView {
    val viewModel by lazy {
        ViewModelProvider(this)[TableScreenPresenter::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        viewModel.requestTable()
    }

    override fun showTable(table: Table) {
        // display items in recycler view
    }
}

interface TableScreenView {
    fun showTable(table: Table)
}


class TableScreenPresenter(
    private val view: TableScreenView,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val tableRequester: suspend () -> Table = { requestTable() }
) : ViewModel() {
    fun requestTable() {
        viewModelScope.launch(backgroundDispatcher) {
            val table = tableRequester()
            viewModelScope.launch(mainDispatcher) {
                view.showTable(table)
            }
        }
    }
}
