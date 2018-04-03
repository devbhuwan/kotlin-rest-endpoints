package io.github.devbhuwan.kotlindslrestendpoints

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class Endpoints(private val repository: OrderRepository) {

    @GetMapping
    fun getOrders(): List<Orders> = repository.findAll()

    @PostMapping
    fun newOrder(@RequestBody order: Orders): Orders = repository.save(order)

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable("id") id: Long): ResponseEntity<Orders> = repository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())


    @PutMapping("/{id}")
    fun updateOrder(@PathVariable("id") id: Long, @RequestBody order: Orders): ResponseEntity<Orders> = repository
            .findById(id).map {
                ResponseEntity.ok(repository.save(it.copy(detail = order.detail)))
            }.orElse(ResponseEntity.notFound().build())

    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable("id") id: Long): ResponseEntity<Void> = repository
            .findById(id).map {
                repository.delete(it)
                ResponseEntity<Void>(HttpStatus.OK)
            }.orElse(ResponseEntity.notFound().build())
}