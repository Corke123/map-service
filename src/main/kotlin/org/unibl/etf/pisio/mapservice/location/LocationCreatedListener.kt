package org.unibl.etf.pisio.mapservice.location

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import mu.two.KotlinLogging
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.unibl.etf.pisio.mapservice.config.MapServiceProperties
import org.unibl.etf.pisio.mapservice.config.RabbitMQProperties
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class LocationCreatedListener(
    private val locationRepository: LocationRepository,
    private val mapServiceProperties: MapServiceProperties,
    val template: RabbitTemplate,
    @Qualifier("notificationExchange") val exchange: Exchange,
    val rabbitMQProperties: RabbitMQProperties
) {

    @EventListener
    fun handleLocationCreatedEvent(locationCreatedEvent: LocationCreatedEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            val location = locationCreatedEvent.location
            val notificationCandidates = locationRepository.findAllWithinRadiusInLastDay(
                location.coordinate, mapServiceProperties.defaultRadius
            ).toList()
            logger.info("Found ${notificationCandidates.size} potential duplicates")
            if (notificationCandidates.isNotEmpty()) {
                template.convertAndSend(
                    exchange.name,
                    rabbitMQProperties.notificationRoutingKey,
                    NotificationCreatedEvent(location.id!!, notificationCandidates)
                )
            }
        }
    }
}

data class LocationCreatedEvent(val location: Location)

data class NotificationCreatedEvent(val incidentId: UUID, val relatedIncidents: List<UUID>)