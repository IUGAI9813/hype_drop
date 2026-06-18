package org.example.hypedrop.repository

import org.example.hypedrop.domain.Order
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderRepository : ReactiveCrudRepository<Order, Long> {
    fun findByUserId(userId: String): Flux<Order>
}