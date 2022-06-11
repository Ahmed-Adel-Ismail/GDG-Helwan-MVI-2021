package com.mvi.core.repositories

import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest

interface PointsOfInterestsRepository {
    suspend fun requestPointsOfInterests(location: LocationInfo): List<PointOfInterest> = listOf()
}