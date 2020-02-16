package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.boolean

class BooleanTest : FunSpec({
   test("should return all booleans") {
      Exhaustive.boolean().values shouldBe listOf(true, false)
   }
})
