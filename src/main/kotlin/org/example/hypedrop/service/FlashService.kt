package org.example.hypedrop.service

import org.example.hypedrop.controller.PurchaseRequest
import org.example.hypedrop.domain.FlashSaleEvent
import org.example.hypedrop.domain.Order
import org.example.hypedrop.dto.UpdateFlashSaleRequest
import org.example.hypedrop.repository.FlashSaleEventRepository
import org.example.hypedrop.repository.OrderRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
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

    fun deleteFlashSaleEvent(id: Long): Mono<Void> {
        return flashSaleEventRepository.findById(id).switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))).flatMap {
         flashSaleEventRepository.deleteById(id)
        }
    }

    fun updateFlash(id: Long  , flash: UpdateFlashSaleRequest): Mono<FlashSaleEvent> {
       return flashSaleEventRepository.findById(id).switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))).flatMap {
           fs ->  val updated = fs.copy(
           productName = flash.productName ?: fs.productName,
           totalStock = flash.totalStock ?: fs.totalStock,
           remainingStock = flash.remainingStock ?: fs.remainingStock,
           startTime = flash.startTime ?: fs.startTime,
           status = flash.status ?: fs.status
       )
           flashSaleEventRepository.save(updated);
       }
    }

  fun getFlash(id:Long): Mono<FlashSaleEvent>{
      return flashSaleEventRepository.findById(id)
  }

    fun purchase(id: Long, userId: String) : Mono<Order> {
        return flashSaleEventRepository.findById(id).flatMap { fs ->  if (fs.remainingStock <= 0) {

            Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "SOLD OUT ITEM WITH ID $id"))
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