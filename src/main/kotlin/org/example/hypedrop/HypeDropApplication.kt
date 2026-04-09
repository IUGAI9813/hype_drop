package org.example.hypedrop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = ["org.example.hypedrop.repository"])
class HypeDropApplication

fun main(args: Array<String>) {
    runApplication<HypeDropApplication>(*args)
}