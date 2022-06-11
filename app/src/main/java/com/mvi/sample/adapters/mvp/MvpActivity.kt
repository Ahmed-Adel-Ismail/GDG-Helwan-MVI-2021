package com.mvi.sample.adapters.mvp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest
import com.mvi.core.ports.PresentationPortMvpView
import com.mvi.sample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MvpActivity : AppCompatActivity(), PresentationPortMvpView {

    private val detectLocationButton: Button by lazy { TODO() }
    private val presenter by lazy {
        ViewModelProvider(this, PresentationAdapterMvpFactory(this))
            .get(PresentationAdapterMvp::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detectLocationButton.setOnClickListener {
            presenter.detectUserLocation()
        }

        presenter.detectUserLocation()
    }

    override fun onRenderUserLocation(userLocation: LocationInfo) {
        lifecycleScope.launch(Dispatchers.Main) {
            // draw user location on map
        }
    }

    override fun onRenderProgress(progressing: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            // update progressing view
        }
    }

    override fun onRenderNearByPointOfInterest(nearByPoint: List<PointOfInterest>) {
        lifecycleScope.launch(Dispatchers.Main) {
            // update recycler view
        }
    }

    override fun onRenderError(throwable: Throwable) {
        lifecycleScope.launch(Dispatchers.Main) {
            // show error based on Exception type
        }
    }
}