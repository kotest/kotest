package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.string.shouldBeUUID
import io.kotest.matchers.string.shouldNotBeUUID
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll

class ShouldBeUUIDTest : FreeSpec({

   "Should be UUID" - {
      "Should pass for Java generated UUIDs" {
         Arb.uuid().checkAll { uuid ->
            uuid.toString().shouldBeUUID()
            uuid.toString().toUpperCase().shouldBeUUID()
            uuid.toString().toLowerCase().shouldBeUUID()
            shouldThrow<AssertionError> { uuid.toString().shouldNotBeUUID() }
         }
      }

      "Should pass for nil UUID" {
         "00000000-0000-0000-0000-000000000000".shouldBeUUID()
         shouldThrow<AssertionError> { "00000000-0000-0000-0000-000000000000".shouldNotBeUUID() }
      }

      "Should fail for nil UUID if it should be considered invalid" {
         shouldThrow<AssertionError> { "00000000-0000-0000-0000-000000000000".shouldBeUUID(considerNilValid = false) }
         "00000000-0000-0000-0000-000000000000".shouldNotBeUUID(considerNilValid = false)
      }

      "Should fail for strings" {
         Arb.string(31, 41).checkAll(iterations = 10_000) { str ->
            shouldThrow<AssertionError> { str.shouldBeUUID() }
            str.shouldNotBeUUID()
         }
      }

      "Should fail for UUIDs without hyphens (not in accordance with specification)" {
         Arb.uuid().checkAll { uuid ->
            val nonHyphens = uuid.toString().replace("-", "")
            nonHyphens.shouldNotBeUUID()
            shouldThrow<AssertionError> { nonHyphens.shouldBeUUID() }
         }
      }
   }
})
