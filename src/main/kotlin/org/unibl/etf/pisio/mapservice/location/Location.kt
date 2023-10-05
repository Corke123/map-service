package org.unibl.etf.pisio.mapservice.location

import java.time.OffsetDateTime
import java.util.UUID

data class Location(var id: UUID?, val incidentId: UUID, val coordinate: Coordinate, val createdAt: OffsetDateTime)

data class Coordinate(val latitude: Double, val longitude: Double)
