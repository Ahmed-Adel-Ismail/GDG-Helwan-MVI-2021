package com.mvi.sample.adapters.mvi

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest
import com.mvi.core.ports.NullableWrapper
import com.mvi.sample.R
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MviActivity : AppCompatActivity() {

    private val progressBar: View by lazy { TODO() }
    private val detectLocationButton: Button by lazy { TODO() }
    private val viewModel by lazy { ViewModelProvider(this)[PresentationAdapterMvi::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.viewStatesStream.asStateFlow().filterNotNull().collect { viewState ->
                progressBar.visibility = View.GONE
                renderRecyclerView(viewState.nearbyPointsOfInterest)
                renderLocationOnMap(viewState.userLocation)
                renderErrorViewIfPresent(viewState.error)
                detectLocationButton.setOnClickListener {
                    viewModel.intentsStream.tryEmit(Intents.OnDetectUserLocation(viewState))
                }
            }
        }
    }

    private fun renderErrorViewIfPresent(error: NullableWrapper<Throwable>?) {
        TODO("Not yet implemented")
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

}