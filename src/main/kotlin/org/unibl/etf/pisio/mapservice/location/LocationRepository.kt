package org.unibl.etf.pisio.mapservice.location

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LocationRepository {
    suspend fun save(location: Location): Location

    fun findAllIdsWithinRadius(coordinate: Coordinate, radius: Double): Flow<UUID>

    fun findAllWithinRadiusInLastDay(coordinate: Coordinate, radius: Double): Flow<UUID>

}