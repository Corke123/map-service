package org.unibl.etf.pisio.mapservice.config

import org.springframework.amqp.core.*
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(RabbitMQProperties::class)
class AmqpConfiguration {

    @Bean
    fun locationQueue(rabbitMQProperties: RabbitMQProperties) = Queue(rabbitMQProperties.locationQueue)

    @Bean
    fun locationExchange(rabbitMQProperties: RabbitMQProperties): Exchange =
        ExchangeBuilder.directExchange(rabbitMQProperties.locationExchange).build()

    @Bean
    fun locationBinding(
        @Qualifier("locationQueue") queue: Queue,
        @Qualifier("locationExchange") exchange: Exchange,
        rabbitMQProperties: RabbitMQProperties
    ): Binding =
        BindingBuilder.bind(queue).to(exchange).with(rabbitMQProperties.locationExchange).noargs()

    @Bean
    fun notificationQueue(rabbitMQProperties: RabbitMQProperties) =
        Queue(rabbitMQProperties.notificationQueue)

    @Bean
    fun notificationExchange(rabbitMQProperties: RabbitMQProperties): Exchange =
        ExchangeBuilder.directExchange(rabbitMQProperties.notificationExchange).build()

    @Bean
    fun notificationBinding(
        @Qualifier("notificationQueue") queue: Queue,
        @Qualifier("notificationExchange") exchange: Exchange,
        rabbitMQProperties: RabbitMQProperties
    ): Binding =
        BindingBuilder.bind(queue).to(exchange).with(rabbitMQProperties.notificationRoutingKey).noargs()

    @Bean
    fun producerJackson2MessageConverter() = Jackson2JsonMessageConverter()
}

@ConfigurationProperties(prefix = "map-service.rabbitmq")
data class RabbitMQProperties(
    val locationQueue: String,
    val locationExchange: String,
    val locationRoutingKey: String,
    val notificationQueue: String,
    val notificationExchange: String,
    val notificationRoutingKey: String
)
