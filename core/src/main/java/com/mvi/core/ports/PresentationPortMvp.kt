package com.mvi.core.ports

import com.mvi.core.CoreDependencies
import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest
import com.mvi.core.repositories.LocationsRepository
import com.mvi.core.repositories.PointsOfInterestsRepository

interface PresentationPortMvpView {
    fun onRenderProgress(progressing: Boolean)
    fun onRenderUserLocation(userLocation: LocationInfo)
    fun onRenderNearByPointOfInterest(nearByPoint: List<PointOfInterest>)
    fun onRenderError(throwable: Throwable)
}

interface PresentationPortMvp {
    var view: PresentationPortMvpView
}

/**
 * move the location on map to the user location, and show the near by points of interests
 * for that location
 */
suspend fun PresentationPortMvp.onDetectUserLocation(
    locationsRepository: LocationsRepository = CoreDependencies.locationsRepository,
    pointsOfInterestsRepository: PointsOfInterestsRepository = CoreDependencies.pointsOfInterestsRepository
) {
    view.onRenderProgress(true)
    runCatching {
        requestLocationAndPointsOfInterests(
            locationsRepository,
            pointsOfInterestsRepository
        )
    }.onFailure {
        view.onRenderError(it)
    }
    view.onRenderProgress(false)
}

private suspend fun PresentationPortMvp.requestLocationAndPointsOfInterests(
    locationsRepository: LocationsRepository,
    pointsOfInterestsRepository: PointsOfInterestsRepository
) {
    val location = locationsRepository.detectUserLocation()
    val pointsOfInterest = pointsOfInterestsRepository.requestPointsOfInterests(location)
    view.onRenderUserLocation(location)
    view.onRenderNearByPointOfInterest(pointsOfInterest)
}