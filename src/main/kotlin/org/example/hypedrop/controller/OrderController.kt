package org.example.hypedrop.controller

import org.example.hypedrop.domain.Order
import org.example.hypedrop.dto.UpdateFlashSaleRequest
import org.example.hypedrop.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/order")
class OrderController(private val orderService: OrderService) {


    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Mono<Order> {
        return orderService.getOrder(id)
    }





    @GetMapping("/user/{userId}")
    fun getUser(@PathVariable userId: String): Flux<Order> {
        return orderService.getUserOrders(userId)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long): Mono<Void> {
        return  orderService.deleteOrder(id)
    }



}


