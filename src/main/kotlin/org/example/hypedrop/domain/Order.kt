package org.example.hypedrop.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("orders")
data class Orders(
    @Id val id: Long? = null,
    val flashSaleId: Long,
    val userId: String,
    val status: String = "CONFIRMED",
    val createdAt: LocalDateTime = LocalDateTime.now()
)
