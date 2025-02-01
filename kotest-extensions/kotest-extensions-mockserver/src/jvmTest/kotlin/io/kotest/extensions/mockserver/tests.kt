package io.kotest.extensions.mockserver

import com.github.kittinunf.fuel.httpGet
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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
