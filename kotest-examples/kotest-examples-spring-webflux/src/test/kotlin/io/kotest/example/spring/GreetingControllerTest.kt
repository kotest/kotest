package io.kotest.example.spring

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@SpringBootTest
@AutoConfigureWebTestClient
class GreetingControllerUnitTest(
   @MockkBean private val greetingService: GreetingService,
   private val webTestClient: WebTestClient
) : StringSpec({
   "should return the greeting provided by greeting service" {
      val greeting = Greeting("Welcome someone")

      every { greetingService.greetingFor("someone") } returns Mono.just(greeting)

      webTestClient
         .get()
         .uri("/greet/someone")
         .exchange()
         .expectStatus().isOk
         .expectBody<Greeting>().isEqualTo(greeting)
   }
   "should return a default greeting when greeting service return error" {
      val defaultGreeting = Greeting("This is default greeting.")

      every { greetingService.greetingFor("someone") } returns Mono.error(RuntimeException("Boom Boom!"))

      webTestClient
         .get()
         .uri("/greet/someone")
         .exchange()
         .expectStatus().isOk
         .expectBody<Greeting>().isEqualTo(defaultGreeting)
   }
})
