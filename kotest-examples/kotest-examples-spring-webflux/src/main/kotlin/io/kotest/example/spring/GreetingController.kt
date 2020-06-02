package io.kotest.example.spring

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class GreetingController(private val greetingService: GreetingService) {

   @GetMapping("/greet/{name}")
   fun greet(@PathVariable name: String): Mono<ResponseEntity<Greeting>> {
      val defaultGreeting = Greeting("This is default greeting.")

      return greetingService.greetingFor(name)
         .map { ResponseEntity.ok(it) }
         .onErrorReturn(ResponseEntity.ok(defaultGreeting))
   }
}
