package io.kotest.extensions.mockserver

import com.github.kittinunf.fuel.httpGet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MockServerListenerSinglePortTest : FunSpec({
   listener(MockServerListener(3030))

   test("Should create mock server in specified port") {
      "http://localhost:3030".httpGet().response().second.statusCode shouldBe 404
   }
})

class MockServerListenerMultiPortTest : FunSpec({
   listener(MockServerListener(3030, 3031, 3032))

   test("Should create mock server in multiple ports") {
      listOf("3030", "3031", "3032").forEach {
         "http://localhost:$it".httpGet().response().second.statusCode shouldBe 404
      }
   }
})

class MockServerListenerRandomPortTest : FunSpec({
   val target = listener(MockServerListener())

   test("Should create mock server in a random port") {
      "http://localhost:${target.mockServer?.port}".httpGet().response().second.statusCode shouldBe 404
   }
})
