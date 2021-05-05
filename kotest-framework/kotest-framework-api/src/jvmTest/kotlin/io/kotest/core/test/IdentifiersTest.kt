package io.kotest.core.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IdentifiersTest : FunSpec() {
   init {
      test("should make repeated names unique") {
         Identifiers.uniqueTestName("foo", emptySet()) shouldBe "foo"
         Identifiers.uniqueTestName("foo", setOf("foo")) shouldBe "(1) foo (1)"
         Identifiers.uniqueTestName("foo", setOf("foo", "foo (1)")) shouldBe "(2) foo"
         Identifiers.uniqueTestName("foo", setOf("foo", "foo (1)", "(2) foo")) shouldBe "(3) foo"
         Identifiers.uniqueTestName("foo", setOf("foo", "foo (1")) shouldBe "(1) foo"
      }
   }
}
