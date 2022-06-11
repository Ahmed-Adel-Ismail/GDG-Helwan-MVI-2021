package com.mvi.core.ports

import com.mvi.core.CoreDependencies
import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest
import com.mvi.core.repositories.LocationsRepository
import com.mvi.core.repositories.PointsOfInterestsRepository

interface PresentationPortMvvm {
    var progressing: Boolean?
    var locationOnMap: LocationInfo?
    var nearbyPointsOfInterest: List<PointOfInterest>?
    var errors: Throwable?
}

/**
 * move the location on map to the user location, and show the near by points of interests
 * for that location
 */
suspend fun PresentationPortMvvm.onDetectUserLocation(
    locationsRepository: LocationsRepository = CoreDependencies.locationsRepository,
    pointsOfInterestsRepository: PointsOfInterestsRepository = CoreDependencies.pointsOfInterestsRepository
) {
    progressing = true
    runCatching {
        requestLocationAndPointsOfInterests(
            locationsRepository,
            pointsOfInterestsRepository
        )
    }.onFailure {
        errors = it
    }
    progressing = false
}

private suspend fun PresentationPortMvvm.requestLocationAndPointsOfInterests(
    locationsRepository: LocationsRepository,
    pointsOfInterestsRepository: PointsOfInterestsRepository
) {
    val location = locationsRepository.detectUserLocation()
    val pointsOfInterest = pointsOfInterestsRepository.requestPointsOfInterests(location)
    locationOnMap = location
    nearbyPointsOfInterest = pointsOfInterest
}
