@file:Suppress("DEPRECATION") // Remove when removing http extension

package com.sksamuel.kotest.extensions.http

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.http.http
import io.kotest.extensions.mockserver.MockServerListener
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

class HttpRequestTest : FunSpec({

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
            .withHeader(
               "X-Test", "foo"
            )
      )
   }

   test("get http request") {
      http("/example_get.http") {
         it.status shouldBe HttpStatusCode.OK
      }
   }

   test("post http request") {
      http("/example_post.http") {
         it.status shouldBe HttpStatusCode.Accepted
         it.headers["X-Test"] shouldBe "foo"
      }
   }

   test("post http request with params") {
      http(
         "/example_post_with_params.http",
         mapOf("path" to "login", "username" to "foo")
      ) {
         it.status shouldBe HttpStatusCode.Accepted
         it.headers["X-Test"] shouldBe "foo"
      }
   }

   test("post http request incorrect username") {
      http(
         "/example_post_with_params.http",
         mapOf("path" to "login", "username" to "boo")
      ) {
         it.status shouldBe HttpStatusCode.NotFound
      }
   }

   test("post http request unknown path") {
      http(
         "/example_post_with_params.http",
         mapOf("path" to "loginAgain%41$#", "username" to "foo")
      ) {
         it.status shouldBe HttpStatusCode.NotFound
      }
   }
})
