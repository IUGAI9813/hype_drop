package org.example.hypedrop.dto

import java.time.LocalDateTime

data class UpdateFlashSaleRequest(
    val productName: String? = null,
    val totalStock: Int? = null,
    val remainingStock: Int? = null,
    val startTime: LocalDateTime? = null,
    val status: String? = null
)