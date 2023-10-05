package org.unibl.etf.pisio.mapservice.location

import mu.two.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

private val logger = KotlinLogging.logger {}

@Component
class LocationRoutes {

    @Bean
    fun http(locationService: LocationService) = coRouter {
        GET("/api/v1/locations") {
            val longitude = it.queryParamOrNull("longitude")?.toDoubleOrNull() ?: 17.189169853295002
            val latitude = it.queryParamOrNull("latitude")?.toDoubleOrNull() ?: 44.77334272596885
            val radius = it.queryParamOrNull("radius")?.toDoubleOrNull() ?: 3749.263697616493
            logger.info("Find ids for location on ($longitude,$latitude) within $radius meters")

            val ids = locationService.findAllWithinRadius(Coordinate(longitude, latitude), radius)

            ServerResponse.ok().bodyAndAwait(ids)
        }
    }
}