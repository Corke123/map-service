package org.unibl.etf.pisio.mapservice.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

private const val INSERT_LOCATION = """
    INSERT INTO location (incident_id, coordinate, created_at)
    VALUES (:incidentId, ST_GeographyFromText('POINT(' || :longitude || ' ' || :latitude || ')'), :createdAt)
    """

private const val SELECT_WITHIN_RADIUS = """
    SELECT incident_id i_id  FROM location l
    WHERE ST_DWithin(coordinate::geography, ST_GeographyFromText('POINT(' || :longitude || ' ' || :latitude || ')'), :radius)
    """

private const val SELECT_WITHIN_RADIUS_IN_LAST_DAY =
    "$SELECT_WITHIN_RADIUS AND created_at >= CURRENT_TIMESTAMP - INTERVAL '1 days'"

@Repository
class LocationRepositoryImpl(val databaseClient: DatabaseClient) : LocationRepository {
    override suspend fun save(location: Location): Location {
        return databaseClient.sql(INSERT_LOCATION)
            .bind("incidentId", location.incidentId)
            .bind("longitude", location.coordinate.longitude)
            .bind("latitude", location.coordinate.latitude)
            .bind("createdAt", location.createdAt)
            .filter { statement -> statement.returnGeneratedValues("id") }
            .fetch().first()
            .doOnNext { location.id = UUID.fromString(it["id"].toString()) }
            .thenReturn(location)
            .awaitSingle()
    }

    override fun findAllIdsWithinRadius(coordinate: Coordinate, radius: Double): Flow<UUID> {
        return databaseClient.sql(SELECT_WITHIN_RADIUS)
            .bind("longitude", coordinate.longitude)
            .bind("latitude", coordinate.latitude)
            .bind("radius", radius)
            .fetch()
            .all()
            .bufferUntilChanged { it["i_id"] }
            .flatMap { Mono.just(it[0]["i_id"] as UUID) }
            .asFlow()
    }

    override fun findAllWithinRadiusInLastDay(
        coordinate: Coordinate,
        radius: Double
    ): Flow<UUID> {
        return databaseClient.sql(SELECT_WITHIN_RADIUS_IN_LAST_DAY)
            .bind("longitude", coordinate.longitude)
            .bind("latitude", coordinate.latitude)
            .bind("radius", radius)
            .fetch()
            .all()
            .bufferUntilChanged { it["i_id"] }
            .flatMap { Mono.just(it[0]["i_id"] as UUID) }
            .asFlow()
    }
}
