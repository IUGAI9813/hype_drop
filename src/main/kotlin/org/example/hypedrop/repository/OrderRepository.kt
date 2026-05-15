package org.example.hypedrop.repository

import org.example.hypedrop.domain.Order
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderRepository : ReactiveCrudRepository<Order, Long> {
    fun findByUserId(userId: String): Flux<Order>
}