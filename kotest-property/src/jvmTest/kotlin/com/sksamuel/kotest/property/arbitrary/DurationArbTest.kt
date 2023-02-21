package com.sksamuel.kotest.property.arbitrary

import duration
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.property.Arb
import io.kotest.property.checkAll

class DurationArbTest : StringSpec({
   "not null test" {
      checkAll(Arb.duration()) {
         it.shouldNotBeNull()
      }
   }
})
