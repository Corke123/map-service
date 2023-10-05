package org.unibl.etf.pisio.mapservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
@EnableConfigurationProperties(MapServiceProperties::class)
class MapServiceConfiguration {

    @Bean
    fun clock() = Clock.systemDefaultZone()!!
}

@ConfigurationProperties(prefix = "map-service")
data class MapServiceProperties(val defaultRadius: Double, val retentionPeriod: Long)