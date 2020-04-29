package com.sksamuel.kt.quarkus

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.quarkus.test.junit.QuarkusTest
import javax.inject.Inject

@QuarkusTest
class QuarkusMockTest : FunSpec() {
   @Inject
   lateinit var mockableService: MockableService

   init {
      test("Should inject the mocked bean") {
         mockableService.greet() shouldBe "Welcome"
      }
   }
}
