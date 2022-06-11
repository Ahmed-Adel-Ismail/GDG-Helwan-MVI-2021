package com.mvi.sample.adapters.mvvm

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest
import com.mvi.sample.R
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MvvmActivity : AppCompatActivity() {

    private val detectLocationButton: Button by lazy { TODO() }
    private val viewModel by lazy { ViewModelProvider(this)[PresentationAdapterMvvm::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            viewModel.progressStream.asStateFlow().collect { renderProgressBar(it) }
            viewModel.nearbyPointsOfInterestStream.asStateFlow().collect { renderRecyclerView(it) }
            viewModel.userLocationStream.asStateFlow().collect { renderLocationOnMap(it) }
            viewModel.errorsStream.asSharedFlow().collect { renderErrorView(it) }
            detectLocationButton.setOnClickListener { viewModel.detectUserLocation() }
        }


    }

    private fun renderErrorView(it: Throwable?) {
        TODO("Not yet implemented")
    }

    private fun renderLocationOnMap(locationInfo: LocationInfo?) {
        TODO("Not yet implemented")
    }

    private fun renderRecyclerView(it: List<PointOfInterest>?) {
        TODO("Not yet implemented")
    }

    private fun renderProgressBar(it: Boolean?) {
        TODO("Not yet implemented")
    }
}