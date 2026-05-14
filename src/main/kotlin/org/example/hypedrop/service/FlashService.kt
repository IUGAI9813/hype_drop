package org.example.hypedrop.service

import org.example.hypedrop.domain.FlashSaleEvent
import org.example.hypedrop.domain.Order
import org.example.hypedrop.repository.FlashSaleEventRepository
import org.example.hypedrop.repository.OrderRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class FlashService ( private val flashSaleEventRepository: FlashSaleEventRepository, private val orderRepository: OrderRepository) {

    fun save(productName : String, totalStock : Int,  startTime:LocalDateTime ) : Mono<FlashSaleEvent> {
        return flashSaleEventRepository.save(FlashSaleEvent(
            productName = productName,
            totalStock = totalStock,
            remainingStock = totalStock,
            startTime = startTime
        ))
    }

  fun getFlash(id:Long): Mono<FlashSaleEvent>{
      return flashSaleEventRepository.findById(id)
  }

    fun puchase(id: Long, userId: String) : Mono<Order> {
        return flashSaleEventRepository.findById(id).flatMap { fs ->  if (fs.remainingStock <= 0) {

            Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "SOLD OUT"))
        } else {
            val updated = fs.copy(remainingStock = fs.remainingStock - 1)
            flashSaleEventRepository.save(updated).flatMap {
                val order = Order(flashSaleId = id, userId = userId)
                orderRepository.save(order)
            }
        }
        }
    }
}