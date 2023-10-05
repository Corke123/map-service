package org.unibl.etf.pisio.mapservice.location

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val publisher: ApplicationEventPublisher
) {

    suspend fun create(location: Location) {
        val savedLocation = locationRepository.save(location)
        publisher.publishEvent(LocationCreatedEvent(savedLocation))
    }

    fun findAllWithinRadius(coordinate: Coordinate, radius: Double) =
        locationRepository.findAllIdsWithinRadius(coordinate, radius)
}