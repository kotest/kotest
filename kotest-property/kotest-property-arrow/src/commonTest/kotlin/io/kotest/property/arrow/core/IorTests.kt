package io.kotest.property.arrow.core

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.inspectors.forAtLeastOne

class IorTests : StringSpec({
  "Arb.ior should generate Left, Right & Both" {
    assertSoftly(Arb.list(Arb.ior(Arb.string(), Arb.int()), 100..120).next()) {
      forAtLeastOne {
        it.isRight().shouldBeTrue()
      }
      forAtLeastOne {
        it.isBoth().shouldBeTrue()
      }
      forAtLeastOne {
        it.isLeft().shouldBeTrue()
      }
    }
  }
})
