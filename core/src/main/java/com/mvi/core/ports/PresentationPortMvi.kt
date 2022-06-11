package com.mvi.core.ports

import com.mvi.core.CoreDependencies
import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest
import com.mvi.core.repositories.LocationsRepository
import com.mvi.core.repositories.PointsOfInterestsRepository

interface PresentationPortMvi {

    var cancellable: Cancellable?
    var state: State?

    data class State(
        val userLocation: LocationInfo? = null,
        val nearbyPointsOfInterest: List<PointOfInterest> = listOf(),
        val error: NullableWrapper<Throwable>? = null
    )

}

/**
 * we don't want the value to persist in the instance after being read,
 * so this wrapper nullify the value after first access
 */
class NullableWrapper<T>(private var value: T? = null) {
    val isNotNull: Boolean get() = value != null
    fun getAndSetToNull() = value?.also { value = null }
}

/**
 * move the location on map to the user location, and show the near by points of interests
 * for that location
 */
suspend fun PresentationPortMvi.onDetectUserLocation(
    lastState: PresentationPortMvi.State = PresentationPortMvi.State(),
    locationsRepository: LocationsRepository = CoreDependencies.locationsRepository,
    pointsOfInterestsRepository: PointsOfInterestsRepository = CoreDependencies.pointsOfInterestsRepository
) {
    cancellable?.cancel()
    cancellable = runCancellable {
        val result = detectLocation(lastState, locationsRepository, pointsOfInterestsRepository)
        if (!isCancelled) state = result
    }
}

private suspend fun detectLocation(
    lastState: PresentationPortMvi.State,
    locationsRepository: LocationsRepository,
    pointsOfInterestsRepository: PointsOfInterestsRepository
) = runCatching {
    val location = locationsRepository.detectUserLocation()
    lastState.copy(
        userLocation = location,
        nearbyPointsOfInterest = pointsOfInterestsRepository.requestPointsOfInterests(location),
        error = null
    )
}.getOrElse {
    lastState.copy(error = NullableWrapper(it))
}


/**
 * this function is used with all Mvi ports, not only this one
 */
suspend fun runCancellable(action: suspend Cancellable.() -> Unit): Cancellable {
    val cancellable = Cancellable()
    cancellable.action()
    return cancellable
}

/**
 * this class is used with all Mvi ports, not only this one
 */
class Cancellable {
    var isCancelled = false; private set
    fun cancel() {
        isCancelled = true
    }
}