package com.mvi.core

import com.mvi.core.repositories.CartRepository
import com.mvi.core.repositories.ItemsRepository
import com.mvi.core.repositories.LocationsRepository
import com.mvi.core.repositories.PointsOfInterestsRepository

object CoreDependencies {
    var cartRepository: CartRepository = object : CartRepository {}
    var itemsRepository: ItemsRepository = object : ItemsRepository {}
    var locationsRepository: LocationsRepository = object : LocationsRepository {}
    var pointsOfInterestsRepository: PointsOfInterestsRepository = object : PointsOfInterestsRepository {}
}