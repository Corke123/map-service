package org.unibl.etf.pisio.mapservice.extensions

import org.unibl.etf.pisio.mapservice.location.Coordinate
import org.unibl.etf.pisio.mapservice.location.Location
import org.unibl.etf.pisio.mapservice.location.LocationCreated
import java.time.OffsetDateTime

fun LocationCreated.toLocation() = Location(null, id, Coordinate(latitude, longitude), OffsetDateTime.now())
