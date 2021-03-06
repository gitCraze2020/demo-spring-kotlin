package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.support.beans
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args) {
		val ctx = beans {
			bean {
				val repository = ref<ReservationRepository>()
				router {
					GET("/reservations") {
						ServerResponse.ok().body(repository.findAll())
					}
				}
			}
			bean {
				val repository = ref<ReservationRepository>()
				ApplicationListener<ApplicationReadyEvent> {
					val names = Flux.just("Matt", "James", "Joe")
							.map { Reservation( name = it) }
							.flatMap { repository.save(it) }

					repository
							.deleteAll()
							.thenMany(names)
							.thenMany(repository.findAll())
							.subscribe { println (it)}
				}
			}
		}
	}
}

data class Reservation(var id : String? =null, val name: String)

interface ReservationRepository : ReactiveCrudRepository<Reservation, String>
