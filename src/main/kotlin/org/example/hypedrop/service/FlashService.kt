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
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript

@Service
class FlashService (
    private val flashSaleEventRepository: FlashSaleEventRepository,
    private val orderRepository: OrderRepository,
    private val redisTemplate: ReactiveStringRedisTemplate) {

    private val decreaseStockScript: RedisScript<Long> = RedisScript.of(
        """
        local stock = tonumber(redis.call('get', KEYS[1]))
        if not stock then
            return -1
        end
        if stock > 0 then
            redis.call('decrby', KEYS[1], 1)
            return 1
        else
            return 0
        end
        """.trimIndent(),
        Long::class.java
    )

    fun save(productName : String, totalStock : Int,  startTime:LocalDateTime ) : Mono<FlashSaleEvent> {
        return flashSaleEventRepository.save(FlashSaleEvent(
            productName = productName,
            totalStock = totalStock,
            remainingStock = totalStock,
            startTime = startTime
        )
        ).flatMap { savedEvent ->
            redisTemplate.opsForValue().set("flash:stock:${savedEvent.id}", savedEvent.remainingStock.toString()).thenReturn(savedEvent)
        }
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
       }.flatMap {
           savedEvent ->
           redisTemplate.opsForValue().set("flash:stock:${savedEvent.id}", savedEvent.remainingStock.toString()).thenReturn(savedEvent)
       }
    }

  fun getFlash(id:Long): Mono<FlashSaleEvent>{
      return flashSaleEventRepository.findById(id)
  }

    // 5천 요청이 동시 들어오면 재고가 마이너스 되고 주문량 늘어남
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

    //  주문량 문제 해결됨 , 1천 오청 미만 경우에 사용,
    //  1초에 10만 요청이 들어오면 하드디스크 부담, 복잡한 비즈니스 로직에 사용 어려움, 샤딩, 레프리케이션 적용된 경우 서버간 통신이 느림
    fun purchasePostgressAtom(id: Long, userId: String) : Mono<Order> {
        return flashSaleEventRepository.decreaseStock(id).flatMap { updateRows ->
            if (updateRows === 0) {
                Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "SOLD OUT ITEM WITH ID"))
            } else {
                val order = Order(flashSaleId = id, userId = userId)
                orderRepository.save(order)
            }
        }
    }

    // RedisLua 사용 방식
    fun purchaseRedisLua(id: Long, userId: String) : Mono<Order> {
        val redisKey = "flash:stock:$id"

        return  redisTemplate.execute (
            decreaseStockScript,
            listOf(redisKey),
            emptyList<String>()
        ).next()
            .flatMap {
                result ->
                when (result){
                    1L -> {
                        flashSaleEventRepository.decreaseStock(id).flatMap {
                            val order = Order(flashSaleId = id, userId = userId)
                            orderRepository.save(order)
                        }
                    }
                    0L -> {
                        Mono.error(ResponseStatusException(HttpStatus.CONFLICT, "SOLD OUT"))
                    }
                    else -> {
                        Mono.error(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stock not initialized in Redis"))
                    }
                }
            }
    }



}