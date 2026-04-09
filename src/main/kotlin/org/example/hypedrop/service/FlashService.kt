package org.example.hypedrop.service

import org.example.hypedrop.domain.FlashSaleEvent
import org.example.hypedrop.repository.FlashSaleEventRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class FlashService ( private val flashSaleEventRepository: FlashSaleEventRepository) {

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
}