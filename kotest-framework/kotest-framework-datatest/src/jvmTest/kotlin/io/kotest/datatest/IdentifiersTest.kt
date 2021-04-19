package io.kotest.datatest

import io.kotest.core.datatest.Identifiers
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IdentifiersTest : FunSpec() {
   init {
      test("should make repeated names unique") {
         Identifiers.uniqueTestName("foo", listOf()) shouldBe "foo"
         Identifiers.uniqueTestName("foo", listOf("foo")) shouldBe "foo (1)"
         Identifiers.uniqueTestName("foo", listOf("foo", "foo")) shouldBe "foo (2)"
      }
   }
}
