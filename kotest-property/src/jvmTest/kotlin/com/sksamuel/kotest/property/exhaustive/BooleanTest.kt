package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.bools

class BooleanTest : FunSpec({
   test("should return all booleans") {
      Exhaustive.bools().values shouldBe listOf(true, false)
   }
})
