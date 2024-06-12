@file:Suppress("DEPRECATION") // Remove when removing http extension

package com.sksamuel.kotest.extensions.http

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.http.http
import io.kotest.extensions.mockserver.MockServerListener
import io.kotest.matchers.shouldBe
import io.ktor.client.features.*
import io.ktor.http.HttpStatusCode
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import java.util.concurrent.TimeUnit

class LongHttpRequestTest : FunSpec({

   listener(MockServerListener(1080))

   beforeTest {
      MockServerClient("localhost", 1080).`when`(
         request()
            .withMethod("POST")
            .withPath("/login")
            .withHeader("Accept", "application/json")
            .withBody("{username: 'foo', password: 'bar'}")
      ).respond(
         response()
            .withStatusCode(202)
            .withCookie(
               "sessionId", "2By8LOhBmaW5nZXJwcmludCIlMDAzMW"
            )
            .withDelay(TimeUnit.MILLISECONDS, 25)
            .withHeader(
               "X-Test", "foo"
            )
      )
   }

   test("post http request with timeout") {
      shouldThrow<HttpRequestTimeoutException> {
         http("/example_post.http", mapOf(), 10) {
            it.status shouldBe HttpStatusCode.Accepted
            it.headers["X-Test"] shouldBe "foo"
         }
      }
   }

   test("post http request") {
      http("/example_post.http", mapOf(), 5000) {
         it.status shouldBe HttpStatusCode.Accepted
         it.headers["X-Test"] shouldBe "foo"
      }
   }

})
