package org.unibl.etf.pisio.mapservice.location

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.two.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.unibl.etf.pisio.mapservice.extensions.toLocation
import java.beans.ConstructorProperties
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class LocationReceiver(val locationService: LocationService) {

    @RabbitListener(queues = ["#{locationQueue.name}"])
    fun receive(@Payload locationCreated: LocationCreated) {
        logger.info("New message received with payload: $locationCreated")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val location = locationCreated.toLocation()
                locationService.create(location)
            } catch (exception: Exception) {
                logger.error("Error on saving location", exception)
            }
        }
    }

}

data class LocationCreated @ConstructorProperties("id", "longitude", "latitude") constructor(
    val id: UUID, val longitude: Double, val latitude: Double
)