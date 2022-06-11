package com.mvi.core.repositories

import com.mvi.core.entities.LocationInfo

interface LocationsRepository {
    /**
     * use android location detection feature from the data layer, and deal with location
     * detection service as a data source, similar to server or preferences for example
     *
     * @throws Exception if the location is not accessible
     */
    @Throws(Exception::class)
    suspend fun detectUserLocation(): LocationInfo = LocationInfo(0.0, 0.0)
}