package org.example.hypedrop.service

import org.example.hypedrop.domain.Order
import org.example.hypedrop.repository.OrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class OrderService (private val orderRepository: OrderRepository) {

    fun getOrder(id:Long): Mono<Order> {
         return orderRepository.findById(id)
    }

    fun getUserOrders(userId: String): Flux<Order> {
           return orderRepository.findByUserId(userId)
    }
}