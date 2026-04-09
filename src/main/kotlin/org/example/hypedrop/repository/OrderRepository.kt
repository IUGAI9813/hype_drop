package org.example.hypedrop.repository

import org.example.hypedrop.domain.Order
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrderRepository : ReactiveCrudRepository<Order, Long>