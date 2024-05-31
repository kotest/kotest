package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.exhaustive

class BooleanTest : FunSpec({
   test("should return all booleans") {
      Exhaustive.boolean().values shouldBe listOf(true, false)
   }

   test("example") {
      (0..50000).map { "string" }.exhaustive().checkAll { it shouldNotBe "test" }
   }

   test("example2") {
      (0..50000).map { "string" }.exhaustive().checkAll { it shouldNotBeIn setOf("test") }
   }
})
