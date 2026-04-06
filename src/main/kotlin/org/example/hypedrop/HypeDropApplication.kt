package org.example.hypedrop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HypeDropApplication

fun main(args: Array<String>) {
    println("HypeDropApplication")
    runApplication<HypeDropApplication>(*args)
}
