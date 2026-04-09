package org.example.hypedrop.repository

import org.example.hypedrop.domain.FlashSaleEvent
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface FlashSaleEventRepository : ReactiveCrudRepository<FlashSaleEvent, Long>