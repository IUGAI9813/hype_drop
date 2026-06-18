package org.example.hypedrop.repository

import org.example.hypedrop.domain.FlashSaleEvent
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface FlashSaleEventRepository : ReactiveCrudRepository<FlashSaleEvent, Long> {

    @Modifying
    @Query(
        "UPDATE flash_sale_events SET remaining_stock = remaining_stock - 1 WHERE id = :id " +
                "AND remaining_stock > 0"
    )
    fun decreaseStock(id: Long): Mono<Int>

}