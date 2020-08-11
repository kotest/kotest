package com.sksamuel.kotest.js

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan

class DateTest : FunSpec() {
   init {
      test("dates") {
         now() shouldBeGreaterThan 0
      }
   }
}
