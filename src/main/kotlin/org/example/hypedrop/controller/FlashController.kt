package org.example.hypedrop.controller

import org.example.hypedrop.domain.FlashSaleEvent
import org.example.hypedrop.domain.Order
import org.example.hypedrop.dto.UpdateFlashSaleRequest
import org.example.hypedrop.service.FlashService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.LocalDateTime


data class CreateFlashSaleRequest(
    val productName: String,
    val totalStock: Int,
    val startTime: LocalDateTime
)

data class PurchaseRequest (
    val userId: String
)



@RestController
@RequestMapping("/api/v1/flash")
class FlashController(private  val flashService: FlashService) {


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateFlashSaleRequest): Mono<FlashSaleEvent> {

        return flashService.save(
            productName = request.productName,
            totalStock = request.totalStock,
            startTime = request.startTime,
        )
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long): Mono<Void> {
    return   flashService.deleteFlashSaleEvent(id);
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateFlashSaleRequest): Mono<FlashSaleEvent> {
        return flashService.updateFlash(id, request)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Mono<FlashSaleEvent> {
        return  flashService.getFlash(id).switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/{id}/purchase")
    fun purchase(@PathVariable id: Long, @RequestBody request: PurchaseRequest): Mono<Order> {
        return flashService.purchasePostgressAtom(id, request.userId)
    }
}