package io.kotest.extensions.mockserver

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse

class MockServerExtensionSinglePortTest : FunSpec({
   install(MockServerExtension(3030))

   test("Should create mock server in specified port") {
      "http://localhost:3030".httpGet().response().second.statusCode shouldBe 404
   }
})

class MockServerExtensionMultiPortTest : FunSpec({
   install(MockServerExtension(3030, 3031, 3032))

   test("Should create mock server in multiple ports") {
      listOf("3030", "3031", "3032").forEach {
         "http://localhost:$it".httpGet().response().second.statusCode shouldBe 404
      }
   }
})

class MockServerExtensionRandomPortTest : FunSpec({
   val server = install(MockServerExtension())

   test("Should create mock server in a random port") {
      "http://localhost:${server.port}".httpGet().response().second.statusCode shouldBe 404
   }
})

class MockServerRoutesTest : FunSpec({
   val server = install(MockServerExtension())

   beforeTest {
      MockServerClient("localhost", server.port).`when`(
         HttpRequest.request()
            .withMethod("POST")
            .withPath("/login")
            .withHeader("Content-Type", "application/json")
            .withBody("""{"username": "foo", "password": "bar"}""")
      ).respond(
         HttpResponse.response()
            .withStatusCode(202)
      )
   }

   test(":mockserver: should allow to define routes", {
      "http://localhost:${server.port}/login".httpPost()
         .header("Content-Type" to "application/json")
         .body("""{"username": "foo", "password": "bar"}""").response().second.statusCode shouldBe 202
   })
})
