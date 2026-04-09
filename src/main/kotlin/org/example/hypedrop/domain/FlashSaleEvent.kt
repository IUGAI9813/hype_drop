package org.example.hypedrop.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "flash_sale_events")
data class FlashSaleEvent (
    @Id val id: Long? = null,
    val productName: String,
    val totalStock: Int,
    val remainingStock: Int,
    val startTime: LocalDateTime,
    val status: String = "PENDING"
)