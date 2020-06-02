package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import io.kotest.property.checkCoverage

class LongTest : FunSpec({

   test("edgecases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         checkAll<Long, Long> { min, max ->
            if (min < max) {
               classify("run")
               Arb.long(min..max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }
})
